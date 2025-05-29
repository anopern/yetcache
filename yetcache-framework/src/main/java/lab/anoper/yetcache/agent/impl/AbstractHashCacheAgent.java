//package lab.anoper.yetcache.agent.impl;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.util.StrUtil;
//import com.alibaba.fastjson2.annotation.JSONType;
//import com.github.benmanes.caffeine.cache.Cache;
//import com.github.benmanes.caffeine.cache.Caffeine;
//import com.google.common.util.concurrent.Striped;
//import com.yxzq.central.start.redis.service.RedisOperations;
//import com.yxzq.common.cache.agent.IHashCacheAgent;
//import com.yxzq.common.cache.domain.dto.CacheAgentOpsRequest;
//import com.yxzq.common.cache.domain.vo.HashCacheOpsVO;
//import com.yxzq.common.cache.enums.EnumCacheEventType;
//import com.yxzq.common.cache.enums.EnumCacheLevel;
//import com.yxzq.common.cache.hotkey.provider.IHotKeyProvider;
//import com.yxzq.common.cache.mq.event.CacheEvent;
//import com.yxzq.common.cache.ops.IHashCacheAgentOps;
//import com.yxzq.common.cache.properties.BaseCacheAgentProperties;
//import com.yxzq.common.cache.source.IHashCacheSourceServiceV2;
//import com.yxzq.common.cache.utils.CacheRetryUtils;
//import com.yxzq.common.cache.utils.LockUtils;
//import com.yxzq.common.cache.utils.RedisSafeUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.redisson.api.RLock;
//import org.springframework.core.ResolvableType;
//import org.springframework.dao.TransientDataAccessException;
//import org.springframework.data.redis.core.HashOperations;
//import org.springframework.lang.Nullable;
//import org.springframework.retry.annotation.Backoff;
//import org.springframework.retry.annotation.Recover;
//import org.springframework.retry.annotation.Retryable;
//import org.springframework.transaction.support.TransactionSynchronization;
//import org.springframework.transaction.support.TransactionSynchronizationManager;
//
//import javax.annotation.PostConstruct;
//import javax.validation.constraints.NotNull;
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.locks.Lock;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//
//@Slf4j
//@JSONType(ignores = {"resolvableType"})
//public abstract class AbstractHashCacheAgent<E> extends AbstractCacheAgent<E>
//        implements IHashCacheAgent<E>, IHashCacheAgentOps {
//    protected Cache<String, Map<String, E>> cache;
//    protected HashOperations<String, String, E> hashOperations;
//
//    // 构造方法传入，@Autowired("xxx")方式传入
//    protected IHashCacheSourceServiceV2<E> sourceService;
//
//    public AbstractHashCacheAgent(BaseCacheAgentProperties properties,
//                                  IHotKeyProvider hotKeyProvider,
//                                  IHashCacheSourceServiceV2<E> sourceService) {
//        super(properties, hotKeyProvider);
//        this.sourceService = sourceService;
//    }
//
//    @PostConstruct
//    public void init() throws Exception {
//        checkProperties();
//        if (properties.isLocalCacheEnabled()) {
//            this.cache = initCaffeineCache();
//        }
//    }
//
//    @Override
//    public List<E> list(String bizKey) {
//        if (isTenantScoped()) {
//            return list(getCurTenantId(), bizKey);
//        } else {
//            return list(null, bizKey);
//        }
//    }
//
//    @Override
//    public List<E> list(Long tenantId, String bizKey) {
//        if (StrUtil.isBlank(bizKey)) {
//            log.warn("bizKey为空，请求到达，请检查配置，请求bizKey");
//            return null;
//        }
//        checkTenantScoped(tenantId);
//        String key = getKeyFromBizKeyWithTenant(tenantId, bizKey);
//        if (properties.isLocalCacheEnabled()) {
//            // 如果JVM缓存找到，直接返回
//            Map<String, E> dataMap = cache.getIfPresent(key);
//            if (CollUtil.isNotEmpty(dataMap)) {
//                return filterEmptyKey(dataMap);
//            }
//            if (properties.isRedisCacheEnabled()) {
//                // 如果Redis找到，回写JVM，然后再返回
//                dataMap = listFromRedis(tenantId, bizKey);
//                if (CollUtil.isNotEmpty(dataMap)) {
//                    // 回写JVM缓存
//                    safeReplaceJvmCache(key, dataMap);
//                    return filterEmptyKey(dataMap);
//                }
//                // 没有找到数据，从数据源加载，需要回写Redis和JVM缓存
//                dataMap = listFromSource(tenantId, bizKey);
//                if (CollUtil.isNotEmpty(dataMap)) {
//                    safeBatchPutRedisHash(key, dataMap);
//                    safeReplaceJvmCache(key, dataMap);
//                    CacheEvent<?> event = CacheEvent.buildUpdateHashAllEvent(getName(), tenantId, bizKey, dataMap);
//                    publishEvent(event);
//                    return new ArrayList<>(dataMap.values());
//                }
//                preventCachePenetration(key);
//                return null;
//            }
//            // 没有开启Redis缓存，则从数据源加载
//            dataMap = listFromSource(tenantId, bizKey);
//            if (CollUtil.isNotEmpty(dataMap)) {
//                safeReplaceJvmCache(key, dataMap);
//                CacheEvent<?> event = CacheEvent.buildUpdateHashAllEvent(getName(), tenantId, bizKey, dataMap);
//                publishEvent(event);
//                return new ArrayList<>(dataMap.values());
//            }
//            preventCachePenetration(key);
//            return null;
//        } else if (properties.isRedisCacheEnabled()) {
//            // 如果Redis找到，回写JVM，然后再返回
//            Map<String, E> dataMap = listFromRedis(tenantId, bizKey);
//            if (CollUtil.isNotEmpty(dataMap)) {
//                return filterEmptyKey(dataMap);
//            }
//            // 没有找到数据，从数据源加载，需要回写Redis
//            dataMap = listFromSource(tenantId, bizKey);
//            if (CollUtil.isNotEmpty(dataMap)) {
//                diffUpdateRedisHash(key, dataMap);
//                return new ArrayList<>(dataMap.values());
//            }
//            preventCachePenetration(key);
//            return null;
//        } else {
//            log.error("本地缓存和Redis缓存都没有开启，请求到达，请检查配置，请求bizKey：{}", bizKey);
//            return null;
//        }
//    }
//
//    @Override
//    public void evictAllCache(String bizKey) {
//        if (isTenantScoped()) {
//            evictAllCache(getCurTenantId(), bizKey);
//        } else {
//            evictAllCache(null, bizKey);
//        }
//    }
//
//    @Override
//    public void evictAllCache(Long tenantId, String bizKey) {
//        checkTenantScoped(tenantId);
//        String key = getKeyFromBizKeyWithTenant(tenantId, bizKey);
//        if (properties.isRedisCacheEnabled()) {
//            RedisSafeUtils.safeDelete(key, (k) -> redisAgent.removeObject(k));
//        }
//        if (properties.isLocalCacheEnabled()) {
//            cache.invalidate(key);
//            CacheEvent<?> event = CacheEvent.buildDeleteHashAllEvent(properties.getName(), tenantId, bizKey);
//            publishEvent(event);
//        }
//    }
//
//    @Override
//    public void updateCache(E data) {
//        if (isTenantScoped()) {
//            updateCache(getCurTenantId(), data);
//        } else {
//            updateCache(null, data);
//        }
//    }
//
//    @Override
//    public void updateCache(@Nullable Long tenantId, @NotNull E e) {
//        checkTenantScoped(tenantId);
//        String key = getKeyFromBizKeyWithTenant(tenantId, getBizKey(e));
//        String hashKey = getHashKey(e); // 获取 hashKey（通常是子项的 id）
//        try {
//            if (properties.isRedisCacheEnabled()) {
//                // 写入 Redis（推荐用工具封装的 safe 方法带异常保护）
//                RedisSafeUtils.safeHashSet(key, hashKey, e,
//                        (k, hk, v) -> getHashOperations().put(k, hk, v));
//                redisAgent.setExpire(key, Long.valueOf(properties.getRedisExpireSecs()));
//            }
//            if (properties.isLocalCacheEnabled()) {
//                // 写入 JVM 缓存
//                safePutJvmCache(key, hashKey, e);
//                // 发送消息，让其他实例也更新
//                CacheEvent<E> event = CacheEvent.buildUpdateOneEvent(properties.getName(), tenantId, e);
//                publishEvent(event);
//            }
//        } catch (Exception ex) {
//            log.error("更新缓存失败，立即驱逐缓存，key: {}, hashKey: {}, data: {}", key, hashKey, e, ex);
//            evictAllCache(tenantId, getBizKey(e));
//        }
//    }
//
//    private void safeBatchPutRedisHash(String key, Map<String, E> dataMap) {
//        RedisSafeUtils.safeBatchPutHash(key, dataMap, Math.toIntExact(properties.getRedisExpireSecs()),
//                100, (k, dm, es, bs)
//                        -> redisAgent.batchPutHash(k, dm, es, bs, getHashOperations()));
//    }
//
//    private Map<String, E> safeBatchGetRedisHash(String key) {
//        return RedisSafeUtils.safeBatchGetHash(key, 100,
//                (k, bs) -> redisAgent.batchGetHashData(k, 100, getHashOperations()));
//    }
//
//    @Override
//    public void refreshAllCache(String bizKey) {
//        if (isTenantScoped()) {
//            refreshAllCache(getCurTenantId(), bizKey);
//        } else {
//            refreshAllCache(null, bizKey);
//        }
//    }
//
//    @Override
//    public void refreshAllCache(Long tenantId, String bizKey) {
//        checkTenantScoped(tenantId);
//        String key = getKeyFromBizKeyWithTenant(tenantId, bizKey);
//        try {
//            // 1. 从数据源（如 DB 或远程接口）加载全量数据
//            Map<String, E> dataMap = listFromSource(tenantId, true, bizKey);
//            if (CollUtil.isEmpty(dataMap)) {
//                log.warn("刷新缓存时，数据为空，bizKey: {}", bizKey);
//                // 删除现有缓存（避免返回陈旧数据）
//                evictAllCache(tenantId, bizKey);
//                return;
//            }
//
//            // 2. 差量更新Redis Hash数据
//            if (properties.isRedisCacheEnabled()) {
//                redisAgent.batchPutHash(key, dataMap, Math.toIntExact(properties.getRedisExpireSecs()),
//                        100, getHashOperations());
//                diffUpdateRedisHash(key, dataMap);
//            }
//
//            if (properties.isLocalCacheEnabled()) {
//                // 3. 更新本地缓存
//                cache.put(key, dataMap);
//                // 4. 发送消息，让其他实例也更新
//                CacheEvent<?> event = CacheEvent.buildUpdateHashAllEvent(properties.getName(), tenantId, bizKey);
//                publishEvent(event);
//            }
//        } catch (Exception e) {
//            log.error("刷新缓存失败，bizKey: {}", bizKey, e);
//            // 可选：发生异常时可选择驱逐旧缓存，强制下次 get 时回源
//            evictAllCache(tenantId, bizKey);
//        }
//    }
//
//    private void diffUpdateRedisHash(String key, Map<String, E> sourceDataMap) {
//        // 3. 更新 Redis 缓存（Hash结构）
//        safeBatchPutRedisHash(key, sourceDataMap);
//
//        // 4. 删除无效的HashKey
//        Set<String> sourceHashKeys = sourceDataMap.keySet();
//        Set<String> redidHashKeys = getHashOperations().keys(key);
//        Set<String> dirtyHashKeys = new HashSet<>(redidHashKeys);
//        dirtyHashKeys.removeAll(sourceHashKeys);
//        if (CollUtil.isNotEmpty(dirtyHashKeys)) {
//            RedisSafeUtils.safeHashDelete(key, dirtyHashKeys,
//                    (k, hks) -> getHashOperations().delete(k, hks));
//        }
//    }
//
//    protected Map<String, E> listFromRedis(Long tenantId, String bizKey) {
//        String key = getKeyFromBizKeyWithTenant(tenantId, bizKey);
//        return RedisSafeUtils.safeBatchGetHash(key, 100,
//                (k, bs) -> redisAgent.batchGetHashData(k, 100, getHashOperations()));
//    }
//
//    protected Map<String, E> listFromSource(Long tenantId, String bizKey) {
//        return listFromSource(tenantId, false, bizKey);
//    }
//
//    /**
//     * 获取缓存数据（支持缓存击穿防护）
//     * <p>当缓存未命中时，通过 Redisson 分布式锁控制并发回源，从而避免缓存击穿</p>
//     *
//     * @param bizKey 业务主键
//     * @return 缓存数据对象，若源数据为空则返回 null
//     */
//    protected Map<String, E> listFromSource(Long tenantId, boolean force, String bizKey) {
//        String lockKey = getQuerySourceDistrLockKey(tenantId, bizKey);
//        RLock lock = redissonClient.getLock(lockKey);
//        try {
//            String key = getKeyFromBizKeyWithTenant(tenantId, bizKey);
//            if (force) {
//                List<E> list = sourceService.queryList(tenantId, bizKey);
//                if (CollUtil.isNotEmpty(list)) {
//                    return list.stream().collect(Collectors.toMap(this::getHashKey, Function.identity()));
//                }
//                return null;
//            }
//            // 最多等 100ms 尝试拿锁。拿到锁后持有 5s，防止死锁
//            Map<String, E> dataMap;
//            if (lock.tryLock(100, 5, TimeUnit.SECONDS)) {
//                // 加锁后再检查一次 Redis，防止缓存已被其他线程更新
//                dataMap = redisAgent.batchGetHashData(key, 100, getHashOperations());
//                if (CollUtil.isNotEmpty(dataMap)) {
//                    return dataMap;
//                }
//
//                // 从数据源加载
//                List<E> list = sourceService.queryList(tenantId, bizKey);
//                if (CollUtil.isNotEmpty(list)) {
//                    dataMap = list.stream().collect(Collectors.toMap(this::getHashKey, Function.identity()));
//                }
//                return dataMap;
//            } else {
//                log.warn("未获取到分布式锁，bizKey: {}, 正在等待其他线程回源填充缓存", bizKey);
//                return CacheRetryUtils.retryRedisGet(() -> redisAgent.batchGetHashData(key, 100, getHashOperations()),
//                        3, 50);
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            throw new RuntimeException("Interrupted while waiting for lock", e);
//        } finally {
//            LockUtils.safeUnlock(lock);
//        }
//    }
//
//    protected Cache<String, Map<String, E>> initCaffeineCache() {
//        return Caffeine.newBuilder()
//                .maximumSize(properties.getJvmMaxSize())
//                .expireAfterWrite(properties.getCaffeineExpireSecs(), TimeUnit.SECONDS)
//                .build();
//    }
//
//    @SuppressWarnings("unchecked")
//    protected HashOperations<String, String, E> getHashOperations() {
//        if (hashOperations == null) {
//            ResolvableType type = getResolvableType();
//            Class<E> clazz = (Class<E>) type.getGeneric(0).resolve();
//            RedisOperations<E> redisOperations = new RedisOperations<E>() {
//                @Override
//                public Class<E> getEntityClass() {
//                    return clazz;
//                }
//            };
//            hashOperations = redisOperations.getHashOperations();
//        }
//        return hashOperations;
//    }
//
//    protected abstract String getHashKey(@NotNull E e);
//
//    public void handleMessage(String message) {
//        if (!properties.isLocalCacheEnabled()) {
//            log.warn("没有开启本地缓存，但是收到了事件消息，消息：{}", message);
//            return;
//        }
//        // 解析出不包含泛型的事件对象
//        CacheEvent<?> rwaEvent = parseRawCacheEvent(message);
//        if (Objects.equals(EnumCacheEventType.UPDATE_ONE.getKey(), rwaEvent.getEventType())) {
//            log.info("开始处理UPDATE_ONE事件，消息：{}", message);
//            handleUpdateOne(message);
//        } else if (Objects.equals(EnumCacheEventType.DELETE_ONE.getKey(), rwaEvent.getEventType())) {
//            log.info("开始处理REMOVE_ONE事件，消息：{}", message);
//            handleDeleteOne(message);
//        } else if (Objects.equals(EnumCacheEventType.UPDATE_HASH_ALL.getKey(), rwaEvent.getEventType())) {
//            log.info("开始处理UPDATE_HASH_ALL事件，消息：{}", message);
//            handleUpdateHashAll(message);
//        } else if (Objects.equals(EnumCacheEventType.DELETE_HASH_ALL.getKey(), rwaEvent.getEventType())) {
//            log.info("开始处理DELETE_HASH_ALL事件，消息：{}", message);
//            handleDeleteHashAll(message);
//        } else if (Objects.equals(EnumCacheEventType.UPDATE_BATCH.getKey(), rwaEvent.getEventType())) {
//            log.info("开始处理UPDATE_BATCH事件，消息：{}", message);
//            handleUpdateBatch(message);
//        } else {
//            throw new IllegalArgumentException("暂不支持的事件类型，事件类型：" + rwaEvent.getEventType());
//        }
//    }
//
//    private void handleUpdateOne(String message) {
//        CacheEvent<E> event = parseCacheEvent(message);
//        E e = event.getData();
//        if (e == null) {
//            throw new IllegalArgumentException("处理单个的 JVM 缓存事件失败，事件中数据不能为空，事件：" + message);
//        }
//        String key = getKeyFromBizKeyWithTenant(event.getTenantId(), getBizKey(e));
//        String hashKey = getHashKey(e);
//        safePutJvmCache(key, hashKey, e);
//    }
//
//    private void handleDeleteOne(String message) {
//        CacheEvent<E> event = parseCacheEvent(message);
//        String key = getKeyFromBizKeyWithTenant(event.getTenantId(), event.getBizKey());
//        removeFromJvmCache(key, event.getHashKey());
//    }
//
//    private void handleUpdateHashAll(String message) {
//        CacheEvent<Map<String, E>> event = parseCacheEventWithMapPayload(message);
//        Map<String, E> dataMap = event.getData();
//        String key = getKeyFromBizKeyWithTenant(event.getTenantId(), event.getBizKey());
//        if (CollUtil.isEmpty(dataMap)) {
//            dataMap = listFromRedis(event.getTenantId(), event.getBizKey());
//        }
//        safePutAllJvmCache(key, dataMap);
//    }
//
//    private void handleDeleteHashAll(String message) {
//        CacheEvent<Map<String, E>> event = parseCacheEventWithMapPayload(message);
//        String key = getKeyFromBizKeyWithTenant(event.getTenantId(), event.getBizKey());
//        cache.invalidate(key);
//    }
//
//    private void handleUpdateBatch(String message) {
//        CacheEvent<Map<String, E>> event = parseCacheEventWithMapPayload(message);
//        Map<String, E> dataMap = event.getData();
//        String key = getKeyFromBizKeyWithTenant(event.getTenantId(), event.getBizKey());
//        safePutAllJvmCache(key, dataMap);
//    }
//
//    protected void safePutJvmCache(String key, String hashKey, E e) {
//        cache.asMap()
//                .computeIfAbsent(key, k -> new ConcurrentHashMap<>())
//                .put(hashKey, e);
//    }
//
//    private final Striped<Lock> stripedLocks = Striped.lock(1024);
//
//    public void safeReplaceJvmCache(String key, Map<String, E> newData) {
//        Lock lock = stripedLocks.get(key);
//        lock.lock();
//        try {
//            cache.put(key, new ConcurrentHashMap<>(newData));
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    public void safePutAllJvmCache(String key, Map<String, E> newData) {
//        cache.asMap().compute(key, (k, existing) -> {
//            if (existing == null) return new ConcurrentHashMap<>(newData);
//            existing.putAll(newData);
//            return existing;
//        });
//    }
//
//    protected void removeFromJvmCache(String key, String hashKey) {
//        Map<String, E> dataMap = cache.getIfPresent(key);
//        if (dataMap != null) {
//            dataMap.remove(hashKey);
//            if (dataMap.isEmpty()) {
//                cache.invalidate(key);
//            }
//        }
//    }
//
//    public void updateCacheAfterCommit(@NotNull E data) {
//        updateCacheAfterCommit(getCurTenantId(), data);
//    }
//
//    /**
//     * 在事务提交后更新缓存
//     *
//     * @param data 更新的缓存数据
//     * @throws IllegalArgumentException 如果当前方法不是在事务中调用会抛出异常
//     */
//    public void updateCacheAfterCommit(@Nullable Long tenantId, @NotNull E data) {
//        if (TransactionSynchronizationManager.isActualTransactionActive()) {
//            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//                @Override
//                public void afterCommit() {
//                    log.debug("Transaction committed, updating cache for: {}", data);
//                    updateCache(tenantId, data);
//                }
//            });
//        } else {
//            throw new IllegalArgumentException("updateCacheAfterCommit 需要在事务中调用！");
//        }
//    }
//
//    protected CacheEvent<Map<String, E>> parseCacheEventWithMapPayload(String message) {
//        return JSON.parseObject(message, getCacheEventMapType());
//    }
//
//    protected Type getCacheEventMapType() {
//        Class<?> entityClass = resolveGenericTypeArgument();
//        ResolvableType mapType = ResolvableType.forClassWithGenerics(Map.class, String.class, entityClass);
//        return ResolvableType.forClassWithGenerics(CacheEvent.class, mapType).getType();
//    }
//
//    /**
//     * 从 Redis 中加载所有热点业务数据到本地（JVM）缓存。
//     * <p>
//     * 热点数据的 bizKey 列表由 {@link IHotKeyProvider#listAllBizKeys(Long)} 提供，
//     * 遍历每个 key，通过统一的 {@link #list(Long, String)} 方法触发本地缓存加载逻辑，
//     * 该方法默认走：本地缓存 → Redis → 数据源 的三级加载链。
//     * </p>
//     * <p>
//     * 如果热点 key 列表为空，仅记录日志并跳过加载流程。
//     * </p>
//     */
//    public void loadFromRedis() {
//        if (isTenantScoped()) {
//            for (Long tenantId : tenantIdProvider.listAllTenantIds()) {
//                loadFromRedis(tenantId);
//            }
//        } else {
//            loadFromRedis(null);
//        }
//    }
//
//    private void loadFromRedis(Long tenantId) {
//        Set<String> hotBizKeys = hotKeyProvider.listAllBizKeys(tenantId);
//        if (CollUtil.isEmpty(hotBizKeys)) {
//            log.warn("热点业务键值列表为空，不从Redis加载所有热点数据到本地缓存，请检查热点数据键值维护模块工作是否正常！");
//            return;
//        }
//        for (String bizKey : hotBizKeys) {
//            try {
//                list(tenantId, bizKey);
//            } catch (Exception e) {
//                log.error("从Redis加载热点业务Key到本地缓存失败，bizKey: {}", bizKey, e);
//            }
//        }
//    }
//
//    @Override
//    public void loadFromSource() {
//        if (isTenantScoped()) {
//            for (Long tenantId : tenantIdProvider.listAllTenantIds()) {
//                loadFromSource(tenantId);
//            }
//        } else {
//            loadFromSource(null);
//        }
//    }
//
//    /**
//     * 重新加载所有热点业务数据到 Redis 和本地缓存。
//     *
//     * <p>流程说明：
//     * 1. 从热点key提供者获取所有热点业务key列表。
//     * 2. 按照每批100个key分批查询数据源。
//     * 3. 对每批查询结果：
//     * - 如果返回空，说明对应业务数据已删除，执行缓存清理。
//     * - 如果有数据，更新缓存。
//     * 4. 对每批查询过程捕获异常，避免单批异常影响整体。
//     *
//     * <p>注意：
//     * - 确保热点key维护模块正常工作。
//     * - 缓存清理可防止缓存脏数据和缓存穿透。
//     */
//
//    @Retryable(value = {TransientDataAccessException.class, IOException.class}, backoff = @Backoff(delay = 2000))
//    public void loadFromSource(Long tenantId) {
//        if (!loadingFromSource.compareAndSet(false, true)) {
//            log.warn("loadFromSource 已在执行，忽略调用");
//            return;
//        }
//        try {
//            Set<String> bizKeys = hotKeyProvider.listAllBizKeys(tenantId);
//            if (CollUtil.isEmpty(bizKeys)) {
//                log.warn("热点业务键值列表为空，不从Source加载所有热点数据到Redis和本地缓存，请检查热点数据键值维护模块工作是否正常！");
//                return;
//            }
//            List<List<String>> bizKeyBatches = CollUtil.split(bizKeys, 100);
//            for (List<String> bizKeyBatch : bizKeyBatches) {
//                List<E> data = sourceService.queryList(tenantId, bizKeyBatch);
//                if (CollUtil.isEmpty(data)) {
//                    log.warn("从数据源查询列表以重新加载Redis和JVM热点数据，返回列表为空，跳过本批数据加载，bizKey: {}", bizKeys);
//                    // 执行删除缓存，保持缓存与数据源同步
//                    for (String bizKey : bizKeyBatch) {
//                        evictAllCache(tenantId, bizKey);
//                    }
//                    continue;
//                }
//                for (E e : data) {
//                    updateCache(tenantId, e);
//                }
//            }
//        } finally {
//            loadingFromSource.set(false);
//        }
//    }
//
//    @Recover
//    public void recover(Exception e) {
//        log.error("loadFromSource 重试失败，放弃重试", e);
//        // 发送告警、记录告警等
//    }
//
//    @Override
//    public void evictAll(@NotNull CacheAgentOpsRequest req) {
//        evictAllCache(req.getTenantId(), req.getBizKey());
//    }
//
//    @Override
//    public void refreshAll(@NotNull CacheAgentOpsRequest req) {
//        refreshAllCache(req.getTenantId(), req.getBizKey());
//    }
//
//    @Override
//    public String viewAll(@NotNull CacheAgentOpsRequest req) {
//        String bizKey = req.getBizKey();
//        String key = getKeyFromBizKeyWithTenant(req.getTenantId(), bizKey);
//        HashCacheOpsVO<E> jvmCacheOpsVO = new HashCacheOpsVO<>(EnumCacheLevel.JVM_CACHE.name(), bizKey);
//        if (properties.isLocalCacheEnabled()) {
//            Map<String, E> dataMap = cache.getIfPresent(key);
//            if (CollUtil.isNotEmpty(dataMap)) {
//                for (String hashKey : dataMap.keySet()) {
//                    E e = dataMap.get(hashKey);
//                    HashCacheOpsVO.HashFieldVO<E> field = new HashCacheOpsVO.HashFieldVO<>(hashKey, e);
//                    jvmCacheOpsVO.getFields().add(field);
//                }
//            }
//        }
//        HashCacheOpsVO<E> redisCacheOpsVO = new HashCacheOpsVO<>(EnumCacheLevel.REDIS_CACHE.getLabel(), bizKey);
//        if (properties.isRedisCacheEnabled()) {
//            Map<String, E> dataMap = safeBatchGetRedisHash(key);
//            if (CollUtil.isNotEmpty(dataMap)) {
//                for (String hashKey : dataMap.keySet()) {
//                    E e = dataMap.get(hashKey);
//                    HashCacheOpsVO.HashFieldVO<E> field = new HashCacheOpsVO.HashFieldVO<>(hashKey, e);
//                    redisCacheOpsVO.getFields().add(field);
//                }
//            }
//        }
//        return redisCacheOpsVO.getCacheType() + " - " + redisCacheOpsVO.getKey() +
//                SEPARATOR +
//                JSON.toJSONString(redisCacheOpsVO.getFields()) +
//                SEPARATOR +
//                jvmCacheOpsVO.getCacheType() + " - " + jvmCacheOpsVO.getKey() +
//                SEPARATOR +
//                JSON.toJSONString(jvmCacheOpsVO.getFields());
//    }
//
//    protected void preventCachePenetration(String key) {
//        Map<String, E> emptyDataMap = new HashMap<>();
//        emptyDataMap.put(EMPTY_OBJ_HASH_KEY, getEmptyObject());
//        RedisSafeUtils.safeBatchPutHash(key, emptyDataMap, properties.getEmptyObjExpireSecs(), 100,
//                (k, dm, es, bs)
//                        -> redisAgent.batchPutHash(k, dm, es, bs, getHashOperations()));
//    }
//
//    protected List<E> filterEmptyKey(Map<String, E> dataMap) {
//        if (CollUtil.isNotEmpty(dataMap)) {
//            dataMap.remove(EMPTY_OBJ_HASH_KEY);
//        }
//        return new ArrayList<>(dataMap.values());
//    }
//}
