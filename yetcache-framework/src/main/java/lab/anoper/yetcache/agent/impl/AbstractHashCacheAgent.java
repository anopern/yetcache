package lab.anoper.yetcache.agent.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONType;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.Striped;
import lab.anoper.yetcache.agent.IHashCacheAgent;
import lab.anoper.yetcache.enums.CacheEventType;
import lab.anoper.yetcache.mq.event.CacheEvent;
import lab.anoper.yetcache.properties.BaseCacheAgentProperties;
import lab.anoper.yetcache.source.IHashCacheSourceService;
import lab.anoper.yetcache.utils.CacheRetryUtils;
import lab.anoper.yetcache.utils.LockUtils;
import lab.anoper.yetcache.utils.RedisSafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.core.ResolvableType;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.lang.Nullable;
import org.springframework.retry.annotation.Recover;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@JSONType(ignores = {"resolvableType"})
public abstract class AbstractHashCacheAgent<E> extends AbstractCacheAgent<E>
        implements IHashCacheAgent<E> {
    protected Cache<String, Map<String, E>> cache;
    protected HashOperations<String, String, E> hashOperations;
    protected IHashCacheSourceService<E> sourceService;

    public AbstractHashCacheAgent(BaseCacheAgentProperties properties,
                                  IHashCacheSourceService<E> sourceService) {
        super(properties);
        this.sourceService = sourceService;
    }

    @PostConstruct
    public void init() throws Exception {
        super.init();
        this.hashOperations = redisTemplate.opsForHash();
        redisTemplate.opsForHash();
        if (properties.isLocalCacheEnabled()) {
            this.cache = initCaffeineCache();
        }
    }

    @Override
    public List<E> list(String bizKey) {
        if (isTenantScoped()) {
            return list(getCurTenantId(), bizKey);
        } else {
            return list(null, bizKey);
        }
    }

    @Override
    public List<E> list(Long tenantId, String bizKey) {
        if (StrUtil.isBlank(bizKey)) {
            log.warn("bizKey为空，请求到达，请检查配置，请求bizKey");
            return null;
        }
        checkTenantScoped(tenantId);
        String key = getKeyFromBizKeyWithTenant(tenantId, bizKey);
        if (properties.isLocalCacheEnabled()) {
            // 如果JVM缓存找到，直接返回
            Map<String, E> dataMap = cache.getIfPresent(key);
            if (CollUtil.isNotEmpty(dataMap)) {
                return filterEmptyKey(dataMap);
            }
            if (properties.isRedisCacheEnabled()) {
                // 如果Redis找到，回写JVM，然后再返回
                dataMap = listFromRedis(tenantId, bizKey);
                if (CollUtil.isNotEmpty(dataMap)) {
                    // 回写JVM缓存
                    safeReplaceJvmCache(key, dataMap);
                    return filterEmptyKey(dataMap);
                }
                // 没有找到数据，从数据源加载，需要回写Redis和JVM缓存
                dataMap = listFromSource(tenantId, bizKey);
                if (CollUtil.isNotEmpty(dataMap)) {
                    safeBatchPutRedisHash(key, dataMap);
                    safeReplaceJvmCache(key, dataMap);
                    CacheEvent<?> event = CacheEvent.buildHashUpdateHashEvent(getId(), tenantId, bizKey, dataMap);
                    publishEvent(event);
                    return new ArrayList<>(dataMap.values());
                }
                preventCachePenetration(key);
                return null;
            }
            // 没有开启Redis缓存，则从数据源加载
            dataMap = listFromSource(tenantId, bizKey);
            if (CollUtil.isNotEmpty(dataMap)) {
                safeReplaceJvmCache(key, dataMap);
                CacheEvent<?> event = CacheEvent.buildHashUpdateHashEvent(getId(), tenantId, bizKey, dataMap);
                publishEvent(event);
                return new ArrayList<>(dataMap.values());
            }
            preventCachePenetration(key);
            return null;
        } else if (properties.isRedisCacheEnabled()) {
            // 如果Redis找到，回写JVM，然后再返回
            Map<String, E> dataMap = listFromRedis(tenantId, bizKey);
            if (CollUtil.isNotEmpty(dataMap)) {
                return filterEmptyKey(dataMap);
            }
            // 没有找到数据，从数据源加载，需要回写Redis
            dataMap = listFromSource(tenantId, bizKey);
            if (CollUtil.isNotEmpty(dataMap)) {
                diffUpdateRedisHash(key, dataMap);
                return new ArrayList<>(dataMap.values());
            }
            preventCachePenetration(key);
            return null;
        } else {
            log.error("本地缓存和Redis缓存都没有开启，请求到达，请检查配置，请求bizKey：{}", bizKey);
            return null;
        }
    }

    @Override
    public void evictAllCache(String bizKey) {
        if (isTenantScoped()) {
            evictAllCache(getCurTenantId(), bizKey);
        } else {
            evictAllCache(null, bizKey);
        }
    }

    @Override
    public void evictAllCache(Long tenantId, String bizKey) {
        checkTenantScoped(tenantId);
        String key = getKeyFromBizKeyWithTenant(tenantId, bizKey);
        if (properties.isRedisCacheEnabled()) {
            RedisSafeUtils.safeDelete(key, (k) -> redisTemplate.delete(k));
        }
        if (properties.isLocalCacheEnabled()) {
            cache.invalidate(key);
            CacheEvent<?> event = CacheEvent.buildHashInvalidateAllFieldsEvent(getId(), tenantId, bizKey);
            publishEvent(event);
        }
    }

    private void safeBatchPutRedisHash(String key, Map<String, E> entries) {
        RedisSafeUtils.safeBatchPutHash(key, entries, Math.toIntExact(properties.getRedisExpireSecs()),
                100, (k, dm, es, bs)
                        -> hashOperations.putAll(k, dm));
        redisTemplate.expire(key, properties.getRedisExpireSecs(), TimeUnit.SECONDS);
    }

    private Map<String, E> safeBatchGetRedisHash(String key) {
        return RedisSafeUtils.safeBatchGetHash(key, 100,
                (k, bs) -> hashOperations.entries(k));
    }

    @Override
    public void refreshAllCache(String bizKey) {
        if (isTenantScoped()) {
            refreshAllCache(getCurTenantId(), bizKey);
        } else {
            refreshAllCache(null, bizKey);
        }
    }

    @Override
    public void refreshAllCache(Long tenantId, String bizKey) {
        checkTenantScoped(tenantId);
        String key = getKeyFromBizKeyWithTenant(tenantId, bizKey);
        try {
            // 1. 从数据源（如 DB 或远程接口）加载全量数据
            Map<String, E> entries = listFromSource(tenantId, true, bizKey);
            if (CollUtil.isEmpty(entries)) {
                log.warn("刷新缓存时，数据为空，bizKey: {}", bizKey);
                // 删除现有缓存（避免返回陈旧数据）
                evictAllCache(tenantId, bizKey);
                return;
            }

            // 2. 差量更新Redis Hash数据
            if (properties.isRedisCacheEnabled()) {
                diffUpdateRedisHash(key, entries);
            }

            if (properties.isLocalCacheEnabled()) {
                // 3. 更新本地缓存
                cache.put(key, entries);
                // 4. 发送消息，让其他实例也更新
                CacheEvent<?> event = CacheEvent.buildHashUpdateHashEvent(getId(), tenantId, bizKey, entries);
                publishEvent(event);
            }
        } catch (Exception e) {
            log.error("刷新缓存失败，bizKey: {}", bizKey, e);
            // 可选：发生异常时可选择驱逐旧缓存，强制下次 get 时回源
            evictAllCache(tenantId, bizKey);
        }
    }

    private void diffUpdateRedisHash(String key, Map<String, E> sourceDataMap) {
        // 3. 更新 Redis 缓存（Hash结构）
        safeBatchPutRedisHash(key, sourceDataMap);

        // 4. 删除无效的HashKey
        Set<String> sourceHashKeys = sourceDataMap.keySet();
        Set<String> redidHashKeys = hashOperations.keys(key);
        Set<String> dirtyHashKeys = new HashSet<>(redidHashKeys);
        dirtyHashKeys.removeAll(sourceHashKeys);
        if (CollUtil.isNotEmpty(dirtyHashKeys)) {
            RedisSafeUtils.safeHashDelete(key, dirtyHashKeys,
                    (k, hks) -> hashOperations.delete(k, hks));
        }
    }

    protected Map<String, E> listFromRedis(Long tenantId, String bizKey) {
        String key = getKeyFromBizKeyWithTenant(tenantId, bizKey);
        return RedisSafeUtils.safeBatchGetHash(key, 100,
                (k, bs) -> hashOperations.entries(k));
    }

    protected Map<String, E> listFromSource(Long tenantId, String bizKey) {
        return listFromSource(tenantId, false, bizKey);
    }

    /**
     * 获取缓存数据（支持缓存击穿防护）
     * <p>当缓存未命中时，通过 Redisson 分布式锁控制并发回源，从而避免缓存击穿</p>
     *
     * @param bizKey 业务主键
     * @return 缓存数据对象，若源数据为空则返回 null
     */
    protected Map<String, E> listFromSource(Long tenantId, boolean force, String bizKey) {
        String lockKey = getQuerySourceDistLockKey(tenantId, bizKey);
        RLock lock = redissonClient.getLock(lockKey);
        try {
            final String key = getKeyFromBizKeyWithTenant(tenantId, bizKey);
            if (force) {
                List<E> list = sourceService.queryList(tenantId, bizKey);
                if (CollUtil.isNotEmpty(list)) {
                    return list.stream().collect(Collectors.toMap(this::getHashKey, Function.identity()));
                }
                return null;
            }
            // 最多等 100ms 尝试拿锁。拿到锁后持有 5s，防止死锁
            Map<String, E> dataMap;
            if (lock.tryLock(100, 5, TimeUnit.SECONDS)) {
                // 加锁后再检查一次 Redis，防止缓存已被其他线程更新
                dataMap = hashOperations.entries(key);
                if (CollUtil.isNotEmpty(dataMap)) {
                    return dataMap;
                }

                // 从数据源加载
                List<E> list = sourceService.queryList(tenantId, bizKey);
                if (CollUtil.isNotEmpty(list)) {
                    dataMap = list.stream().collect(Collectors.toMap(this::getHashKey, Function.identity()));
                }
                return dataMap;
            } else {
                log.warn("未获取到分布式锁，bizKey: {}, 正在等待其他线程回源填充缓存", bizKey);
                return CacheRetryUtils.retryRedisGet(() -> hashOperations.entries(key),
                        3, 50);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for lock", e);
        } finally {
            LockUtils.safeUnlock(lock);
        }
    }

    protected Cache<String, Map<String, E>> initCaffeineCache() {
        return Caffeine.newBuilder()
                .maximumSize(properties.getJvmMaxSize())
                .expireAfterWrite(properties.getCaffeineExpireSecs(), TimeUnit.SECONDS)
                .build();
    }

    protected abstract String getHashKey(@NotNull E e);

    public void handleMessage(String message) {
        if (!properties.isLocalCacheEnabled()) {
            log.warn("没有开启本地缓存，但是收到了事件消息，消息：{}", message);
            return;
        }
        // 解析出不包含泛型的事件对象
        CacheEvent<?> rwaEvent = parseRawCacheEvent(message);
        if (Objects.equals(CacheEventType.UPDATE_ENTRY, rwaEvent.getEventType())) {
            log.info("开始处理UPDATE_FIELD事件，消息：{}", message);
            handleUpdateField(message);
        } else if (Objects.equals(CacheEventType.INVALIDATE_FIELD, rwaEvent.getEventType())) {
            log.info("开始处理INVALIDATE_FIELD事件，消息：{}", message);
            handleInvalidateField(message);
        } else if (Objects.equals(CacheEventType.UPDATE_ALL_FIELDS, rwaEvent.getEventType())) {
            log.info("开始处理UPDATE_ALL_FIELDS事件，消息：{}", message);
            handleUpdateAllFields(message);
        } else if (Objects.equals(CacheEventType.INVALIDATE_ALL_FIELDS, rwaEvent.getEventType())) {
            log.info("开始处理INVALIDATE_ALL_FIELDS事件，消息：{}", message);
            handleInvalidateAllFields(message);
        } else {
            throw new IllegalArgumentException("暂不支持的事件类型，事件类型：" + rwaEvent.getEventType());
        }
    }

    private void handleUpdateField(String message) {
        CacheEvent<E> event = parseCacheEvent(message);
        E e = event.getData();
        if (e == null) {
            throw new IllegalArgumentException("处理单个的 JVM 缓存事件失败，事件中数据不能为空，事件：" + message);
        }
        String key = getKeyFromBizKeyWithTenant(event.getTenantId(), getBizKey(e));
        String hashKey = getHashKey(e);
        safePutJvmCache(key, hashKey, e);
    }

    private void handleInvalidateField(String message) {
        CacheEvent<E> event = parseCacheEvent(message);
        CacheEvent.BizKey bizKey = event.getBizKey();
        String key = getKeyFromBizKeyWithTenant(event.getTenantId(), bizKey.getBizKey());
        removeFromJvmCache(key, bizKey.getBizField());
    }

    private void handleUpdateAllFields(String message) {
        CacheEvent<Map<String, E>> event = parseCacheEventWithMapPayload(message);
        Map<String, E> dataMap = event.getData();
        CacheEvent.BizKey bizKey = event.getBizKey();
        String key = getKeyFromBizKeyWithTenant(event.getTenantId(), bizKey.getBizKey());
        if (CollUtil.isEmpty(dataMap)) {
            dataMap = listFromRedis(event.getTenantId(), bizKey.getBizKey());
        }
        safePutAllJvmCache(key, dataMap);
    }

    private void handleInvalidateAllFields(String message) {
        CacheEvent<Map<String, E>> event = parseCacheEventWithMapPayload(message);
        CacheEvent.BizKey bizKey = event.getBizKey();
        String key = getKeyFromBizKeyWithTenant(event.getTenantId(), bizKey.getBizKey());
        cache.invalidate(key);
    }

    private void handleUpdateBatch(String message) {
        CacheEvent<Map<String, E>> event = parseCacheEventWithMapPayload(message);
        Map<String, E> dataMap = event.getData();
        CacheEvent.BizKey bizKey = event.getBizKey();
        String key = getKeyFromBizKeyWithTenant(event.getTenantId(), bizKey.getBizKey());
        safePutAllJvmCache(key, dataMap);
    }

    protected void safePutJvmCache(String key, String hashKey, E e) {
        cache.asMap()
                .computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                .put(hashKey, e);
    }

    private final Striped<Lock> stripedLocks = Striped.lock(1024);

    public void safeReplaceJvmCache(String key, Map<String, E> newData) {
        Lock lock = stripedLocks.get(key);
        lock.lock();
        try {
            cache.put(key, new ConcurrentHashMap<>(newData));
        } finally {
            lock.unlock();
        }
    }

    public void safePutAllJvmCache(String key, Map<String, E> newData) {
        cache.asMap().compute(key, (k, existing) -> {
            if (existing == null) return new ConcurrentHashMap<>(newData);
            existing.putAll(newData);
            return existing;
        });
    }

    protected void removeFromJvmCache(String key, String hashKey) {
        Map<String, E> dataMap = cache.getIfPresent(key);
        if (dataMap != null) {
            dataMap.remove(hashKey);
            if (dataMap.isEmpty()) {
                cache.invalidate(key);
            }
        }
    }

    public void updateCacheAfterCommit(@NotNull E data) {
        updateCacheAfterCommit(getCurTenantId(), data);
    }

    /**
     * 在事务提交后更新缓存
     *
     * @param data 更新的缓存数据
     * @throws IllegalArgumentException 如果当前方法不是在事务中调用会抛出异常
     */
    public void updateCacheAfterCommit(@Nullable Long tenantId, @NotNull E data) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    log.debug("Transaction committed, updating cache for: {}", data);
                    updateCache(tenantId, data);
                }
            });
        } else {
            throw new IllegalArgumentException("updateCacheAfterCommit 需要在事务中调用！");
        }
    }

    protected CacheEvent<Map<String, E>> parseCacheEventWithMapPayload(String message) {
        return JSON.parseObject(message, getCacheEventMapType());
    }

    protected Type getCacheEventMapType() {
        Class<?> entityClass = resolveGenericTypeArgument();
        ResolvableType mapType = ResolvableType.forClassWithGenerics(Map.class, String.class, entityClass);
        return ResolvableType.forClassWithGenerics(CacheEvent.class, mapType).getType();
    }

    @Recover
    public void recover(Exception e) {
        log.error("loadFromSource 重试失败，放弃重试", e);
        // 发送告警、记录告警等
    }


    protected List<E> filterEmptyKey(Map<String, E> dataMap) {
        if (CollUtil.isNotEmpty(dataMap)) {
            dataMap.remove(EMPTY_OBJ_HASH_KEY);
        }
        return new ArrayList<>(dataMap.values());
    }

    public void updateCache(E data) {
        if (isTenantScoped()) {
            updateCache(getCurTenantId(), data);
        } else {
            updateCache(null, data);
        }
    }

    public void updateCache(@Nullable Long tenantId, @NotNull E e) {
        checkTenantScoped(tenantId);
        String key = getKeyFromBizKeyWithTenant(tenantId, getBizKey(e));
        String hashKey = getHashKey(e); // 获取 hashKey（通常是子项的 id）
        try {
            if (properties.isRedisCacheEnabled()) {
                // 写入 Redis（推荐用工具封装的 safe 方法带异常保护）
                RedisSafeUtils.safeHashSet(key, hashKey, e, (k, hk, v) -> hashOperations.put(k, hk, v));
                redisTemplate.expire(key, properties.getRedisExpireSecs(), TimeUnit.SECONDS);
            }
            if (properties.isLocalCacheEnabled()) {
                // 写入 JVM 缓存
                safePutJvmCache(key, hashKey, e);
                // 发送消息，让其他实例也更新
                String bizKey = getBizKey(e);
                String bizHashField = getHashKey(e);
                CacheEvent<E> event = CacheEvent.buildHashUpdateEntryEvent(getId(), tenantId, bizKey, bizHashField, e);
                publishEvent(event);
            }
        } catch (Exception ex) {
            log.error("更新缓存失败，立即驱逐缓存，key: {}, hashKey: {}, data: {}", key, hashKey, e, ex);
            evictAllCache(tenantId, getBizKey(e));
        }
    }

    protected void preventCachePenetration(String key) {
        Map<String, E> emptyEntries = new HashMap<>();
        emptyEntries.put(EMPTY_OBJ_HASH_KEY, getEmptyObject());
        RedisSafeUtils.safeBatchPutHash(key, emptyEntries, properties.getEmptyObjExpireSecs(), 100,
                (k, dm, es, bs) -> hashOperations.putAll(k, dm));
        redisTemplate.expire(key, properties.getEmptyObjExpireSecs(), TimeUnit.SECONDS);
    }
}
