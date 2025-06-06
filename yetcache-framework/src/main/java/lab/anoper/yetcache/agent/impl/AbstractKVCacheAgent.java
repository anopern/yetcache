package lab.anoper.yetcache.agent.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.alibaba.fastjson2.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lab.anoper.yetcache.agent.IKVCacheAgent;
import lab.anoper.yetcache.enums.CacheEventType;
import lab.anoper.yetcache.mq.event.CacheEvent;
import lab.anoper.yetcache.properties.BaseCacheAgentProperties;
import lab.anoper.yetcache.source.IKVCacheSourceService;
import lab.anoper.yetcache.utils.CacheRetryUtils;
import lab.anoper.yetcache.utils.LockUtils;
import lab.anoper.yetcache.utils.RedisSafeUtils;
import lab.anoper.yetcache.utils.SpringContextProvider;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.retry.annotation.Recover;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * @author walter.yan
 * @since 2025/5/21
 */
@Slf4j
public abstract class AbstractKVCacheAgent<E> extends AbstractCacheAgent<E> implements IKVCacheAgent<E> {

    // agent方法初始化
    protected Cache<String, E> cache;
    protected RedisTemplate<String, E> redisTemplate;
    protected ValueOperations<String, E> valueOperations;

    // 构造方法传入，@Autowired("xxx")方式传入
    protected IKVCacheSourceService<E> sourceService;

    public AbstractKVCacheAgent(BaseCacheAgentProperties properties,
                                IKVCacheSourceService<E> sourceService) {
        super(properties);
        this.sourceService = sourceService;
    }

    @PostConstruct
    public void init() throws Exception {
        checkProperties();
        if (properties.isLocalCacheEnabled()) {
            this.cache = initCaffeineCache();
        }
    }

    @Override
    public E get(String bizKey) {
        if (isTenantScoped()) {
            return get(getCurTenantId(), bizKey);
        } else {
            return get(null, bizKey);
        }
    }

    @Override
    public E get(@Nullable Long tenantId, @NotNull String bizKey) {
        if (StrUtil.isBlank(bizKey)) {
            log.warn("bizKey为空，请求到达，请检查配置，请求bizKey");
            return null;
        }
        checkTenantScoped(tenantId);
        String key = getKeyFromBizKeyWithTenant(tenantId, bizKey);
        if (properties.isLocalCacheEnabled()) {
            E e = cache.getIfPresent(key);
            if (null != e) {
                return e;
            }
            if (properties.isRedisCacheEnabled()) {
                e = getFromRedis(tenantId, bizKey);
                if (null != e) {
                    cache.put(key, e);
                    publishEvent(CacheEvent.buildKVUpdateEvent(getId(), tenantId, e));
                }
                e = getFromSource(tenantId, bizKey);
                if (e != null) {
                    safeRedisSet(key, e);
                    cache.put(key, e);
                    return e;
                }
                preventCachePenetration(key);
                return null;
            }
            e = getFromSource(tenantId, bizKey);
            if (e != null) {
                cache.put(key, e);
                return e;
            }
            preventCachePenetration(key);
            return null;
        } else if (properties.isRedisCacheEnabled()) {
            E e = getFromSource(tenantId, bizKey);
            if (e != null) {
                safeRedisSet(key, e);
                cache.put(key, e);
                return e;
            }
            preventCachePenetration(key);
            return null;
        } else {
            log.error("本地缓存和Redis缓存都没有开启，请求到达，请检查配置，请求bizKey：{}", bizKey);
            return null;
        }
    }

    public void evictCache(@NotNull String bizKey) {
        evictCache(getCurTenantId(), bizKey);
    }

    @Override
    public void evictCache(@Nullable Long tenantId, @NotNull String bizKey) {
        checkTenantScoped(tenantId);
        String key = getKeyFromBizKeyWithTenant(tenantId, bizKey);
        RedisSafeUtils.safeDelete(key, k -> redisTemplate.delete(k));
        cache.invalidate(key);
        publishEvent(CacheEvent.buildKVInvalidateEvent(getId(), tenantId, bizKey));
    }

    @Override
    public void refreshCache(String bizKey) {
        if (isTenantScoped()) {
            refreshCache(getCurTenantId(), bizKey);
        } else {
            refreshCache(null, bizKey);
        }
    }

    @Override
    public void refreshCache(Long tenantId, String bizKey) {
        checkTenantScoped(tenantId);
        String key = getKeyFromBizKeyWithTenant(tenantId, bizKey);
        try {
            // 1. 从数据源（如 DB 或远程接口）加载全量数据
            E e = getFromSource(tenantId, true, bizKey);
            if (null == e) {
                log.warn("刷新缓存时，数据为空，bizKey: {}", bizKey);
                // 删除现有缓存（避免返回陈旧数据）
                evictCache(tenantId, bizKey);
                return;
            }

            // 2. 差量更新Redis Hash数据
            if (properties.isRedisCacheEnabled()) {
                safeRedisSet(key, e);
            }

            if (properties.isLocalCacheEnabled()) {
                // 3. 更新本地缓存
                cache.put(key, e);
                // 4. 发送消息，让其他实例也更新
                CacheEvent<?> event = CacheEvent.buildKVUpdateEvent(properties.getId(), tenantId, e);
                publishEvent(event);
            }
        } catch (Exception e) {
            log.error("刷新缓存失败，bizKey: {}", bizKey, e);
            // 可选：发生异常时可选择驱逐旧缓存，强制下次 get 时回源
            evictCache(tenantId, bizKey);
        }
    }

    @SuppressWarnings("unchecked")
    public ValueOperations<String, E> getValueOperations() {
        if (valueOperations == null) {
            Class<E> clazz = (Class<E>) getResolvableType().getGeneric(0).resolve();
            // clone一个新的 RedisTemplate 并设置序列化器（避免污染全局模板）
            RedisTemplate<String, E> redisTemplate = new RedisTemplate<>();
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(clazz));
            RedisConnectionFactory redisConnectionFactory = springContextProvider.getBean(RedisConnectionFactory.class);
            redisTemplate.setConnectionFactory(redisConnectionFactory);
            redisTemplate.afterPropertiesSet();
            this.redisTemplate = redisTemplate;
            this.valueOperations = redisTemplate.opsForValue();
        }
        return valueOperations;
    }

    protected E getFromRedis(Long tenantId, String bizKey) {
        String key = getKeyFromBizKeyWithTenant(tenantId, bizKey);
        return RedisSafeUtils.safeGet(key, k -> getValueOperations().get(key));
    }

    protected E getFromSource(Long tenantId, String bizKey) {
        return getFromSource(tenantId, false, bizKey);
    }

    /**
     * 获取缓存数据（支持缓存击穿防护）
     * <p>当缓存未命中时，通过 Redisson 分布式锁控制并发回源，从而避免缓存击穿</p>
     *
     * @param bizKey 业务主键
     * @return 缓存数据对象，若源数据为空则返回 null
     */
    protected E getFromSource(Long tenantId, boolean force, String bizKey) {
        String lockKey = getQuerySourceDistLockKey(tenantId, bizKey);
        RLock lock = redissonClient.getLock(lockKey);
        try {
            String key = getKeyFromBizKeyWithTenant(tenantId, bizKey);
            if (force || !properties.isRedisCacheEnabled()) {
                return sourceService.querySingle(tenantId, bizKey);
            } else {
                // 最多等 100ms 尝试拿锁。拿到锁后持有 5s，防止死锁
                if (lock.tryLock(100, 5, TimeUnit.SECONDS)) {
                    // 加锁后再检查一次 Redis，防止缓存已被其他线程更新
                    E e = safeRedisGet(key);
                    if (null != e) {
                        return e;
                    }
                    // 从数据源加载
                    return sourceService.querySingle(tenantId, bizKey);
                } else {
                    log.warn("未获取到分布式锁，bizKey: {}, 正在等待其他线程回源填充缓存", bizKey);
                    CacheRetryUtils.retryRedisGet(() -> getValueOperations().get(key), 3, 50);
                    return null;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for lock", e);
        } finally {
            LockUtils.safeUnlock(lock);
        }
    }

    /**
     * 初始化JVM缓存类F
     */
    protected Cache<String, E> initCaffeineCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(properties.getCaffeineExpireSecs(), TimeUnit.SECONDS)
                .build();
    }

    /**
     * 处理来自消息事件的缓存操作，仅支持单条数据更新和删除操作。
     * <p>
     * 支持的事件类型：
     * <ul>
     *   <li>{@code EnumCacheEventType.UPDATE_ONE}：将数据更新到本地缓存</li>
     *   <li>{@code EnumCacheEventType.REMOVE_ONE}：将数据从本地缓存中移除</li>
     * </ul>
     * <p>
     * 若事件类型不在支持列表中，或事件数据为 {@code null}，则抛出 {@link IllegalArgumentException}。
     *
     * @param message 缓存事件数据对象，包含事件类型和对应的数据实体
     * @throws IllegalArgumentException 如果事件类型不被支持，或数据为 null
     */
    public void handleMessage(String message) {
        CacheEvent<?> event = parseRawCacheEvent(message);
        if (Objects.equals(CacheEventType.UPDATE, event.getEventType())) {
            handleUpdateOneEvent(message);
        } else if (Objects.equals(CacheEventType.INVALIDATE, event.getEventType())) {
            handleDeleteOneEvent(message);
        } else {
            throw new IllegalArgumentException("暂不支持的事件类型，事件类型：" + event.getEventType());
        }
    }

    private void handleUpdateOneEvent(String message) {
        CacheEvent<E> event = parseCacheEvent(message);
        log.info("[JVM缓存]处理单个的JVM缓存[UPDATE_ONE]事件：{}", message);
        E e = event.getData();
        if (e == null) {
            throw new IllegalArgumentException("处理单个的 JVM 缓存事件失败，事件中数据不能为空，事件："
                    + JSON.toJSONString(event));
        }
        String key = getKeyFromBizKeyWithTenant(event.getTenantId(), getBizKey(e));
        cache.put(key, e);
    }

    private void handleDeleteOneEvent(String message) {
        CacheEvent<E> event = parseCacheEvent(message);
        log.info("[JVM缓存]处理单个的JVM缓存[DELETE_ONE]事件：{}", message);
        CacheEvent.BizKey bizKey = event.getBizKey();
        if (null == bizKey || StrUtil.isBlank(bizKey.getBizKey())) {
            throw new IllegalArgumentException("处理删除缓存事件失败，bizKey不能为空，事件："
                    + JSON.toJSONString(event));
        }
        cache.invalidate(getKeyFromBizKeyWithTenant(event.getTenantId(), bizKey.getBizKey()));
    }

//    /**
//     * 从 Redis 中加载所有热点业务数据到本地（JVM）缓存。
//     * <p>
//     * 热点数据的 bizKey 列表由 {@link IHotKeyProvider#listAllBizKeys(Long)} ()} 提供，
//     * 遍历每个 key，通过统一的 {@link #get(Long, String)} 方法触发本地缓存加载逻辑，
//     * 该方法默认走：本地缓存 → Redis → 数据源 的三级加载链。
//     * </p>
//     * <p>
//     * 如果热点 key 列表为空，仅记录日志并跳过加载流程。
//     * </p>
//     */
//    public void loadFromRedis() {
//        if (null == hotKeyProvider) {
//            log.warn("未配置热点数据键值提供者，跳过从Redis加载热点数据到本地缓存");
//            return;
//        }
//        if (isTenantScoped()) {
//            for (Long tenantId : tenantIdProvider.listAllTenantIds()) {
//                loadFromRedis(tenantId);
//            }
//        } else {
//            loadFromRedis(null);
//        }
//    }

//    public void loadFromRedis(Long tenantId) {
//        if (null == hotKeyProvider) {
//            log.warn("未配置热点数据键值提供者，跳过从Redis加载热点数据到本地缓存");
//            return;
//        }
//        Set<String> hotBizKeys = hotKeyProvider.listAllBizKeys(tenantId);
//        if (CollUtil.isEmpty(hotBizKeys)) {
//            log.warn("热点业务键值列表为空，不从Redis加载所有热点数据到本地缓存，请检查热点数据键值维护模块工作是否正常！");
//            return;
//        }
//        for (String bizKey : hotBizKeys) {
//            try {
//                get(tenantId, bizKey);
//            } catch (Exception e) {
//                log.error("从Redis加载热点业务Key到本地缓存失败，bizKey: {}", bizKey, e);
//            }
//        }
//    }

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
//    @Override
//    public void loadFromSource() {
//        if (null == hotKeyProvider) {
//            log.warn("未配置热点数据键值提供者，跳过从Source加载热点数据到Redis和本地缓存");
//            return;
//        }
//        if (isTenantScoped()) {
//            for (Long tenantId : tenantIdProvider.listAllTenantIds()) {
//                loadFromSource(tenantId);
//            }
//        } else {
//            loadFromSource(null);
//        }
//    }
//
//    @Retryable(value = {TransientDataAccessException.class, IOException.class}, backoff = @Backoff(delay = 2000))
//    public void loadFromSource(Long tenantId) {
//        if (null == hotKeyProvider) {
//            log.warn("未配置热点数据键值提供者，跳过从Source加载热点数据到Redis和本地缓存");
//            return;
//        }
//        if (!loadingFromSource.compareAndSet(false, true)) {
//            log.warn("loadFromSource 已在执行，忽略调用");
//            return;
//        }
//        try {
//            Set<String> hotBizKeys = hotKeyProvider.listAllBizKeys(tenantId);
//            if (CollUtil.isEmpty(hotBizKeys)) {
//                log.warn("热点业务键值列表为空，不从Source加载所有热点数据到Redis和本地缓存，请检查热点数据键值维护模块工作是否正常！");
//                return;
//            }
//            List<List<String>> bizKeyBatches = Lists.partition(new ArrayList<>(hotBizKeys), 100);
//            for (List<String> bizKeyBatch : bizKeyBatches) {
//                List<E> data = sourceService.queryList(tenantId, bizKeyBatch);
//                if (CollUtil.isEmpty(data)) {
//                    log.warn("从数据源查询列表以重新加载Redis和JVM热点数据，返回列表为空，跳过本批数据加载，bizKeys: {}",
//                            JSON.toJSONString(bizKeyBatch));
//                    // 执行删除缓存，保持缓存与数据源同步
//                    for (String bizKey : bizKeyBatch) {
//                        evictCache(tenantId, bizKey);
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

    @Recover
    public void recover(Exception e) {
        log.error("loadFromSource 重试失败，放弃重试", e);
        // 发送告警、记录告警等
    }

//    /**
//     * 在事务提交后更新缓存
//     *
//     * @param data 更新的缓存数据
//     * @throws IllegalArgumentException 如果当前方法不是在事务中调用会抛出异常
//     */
//    public void updateCacheAfterCommit(@Nullable Long tenantId, E data) {
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

    private void safeRedisSet(String key, E e) {
        RedisSafeUtils.safeSet(key, e, (k, v)
                -> getValueOperations().set(k, v, properties.getRedisExpireSecs(), TimeUnit.SECONDS));
    }

    private E safeRedisGet(String key) {
        return RedisSafeUtils.safeGet(key, k -> getValueOperations().get(k));
    }

    /**
     * 防止缓存穿透：当查询结果为 null 时，填充一个空对象。
     *
     * @param key Redis 的 key
     */
    protected void preventCachePenetration(String key) {
        E emptyObject = getEmptyObject();
        RedisSafeUtils.safeSet(key, emptyObject, (k, v)
                -> getValueOperations().set(k, v, properties.getEmptyObjExpireSecs(), TimeUnit.SECONDS));
    }
}