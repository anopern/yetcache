package com.yetcache.agent.core.structure.dynamichash;

import cn.hutool.core.collection.CollUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yetcache.agent.broadcast.command.ExecutableCommand;
import com.yetcache.agent.broadcast.publisher.CacheBroadcastPublisher;
import com.yetcache.agent.core.CacheAgentMethod;
import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.interceptor.BehaviorType;
import com.yetcache.agent.interceptor.InvocationChainRegistry;
import com.yetcache.agent.interceptor.DefaultInvocationContext;
import com.yetcache.core.cache.dynamichash.DefaultMultiTierDynamicHashCache;
import com.yetcache.core.cache.dynamichash.MultiTierDynamicHashCache;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.context.CacheAccessContext;
import com.yetcache.core.result.*;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
@Slf4j
public class AbstractDynamicHashCacheAgent<K, F, V> implements DynamicHashCacheAgent<K, F, V> {
    protected final String componentName;
    protected final MultiTierDynamicHashCache<K, F, V> multiTierCache;
    protected final DynamicHashCacheConfig config;
    protected final DynamicHashCacheLoader<K, F, V> cacheLoader;
    private final Cache<K, Long> fullyLoadedTs;
    private final CacheBroadcastPublisher broadcastPublisher;
    private final InvocationChainRegistry chainRegistry;

    public AbstractDynamicHashCacheAgent(String componentNane,
                                         DynamicHashCacheConfig config,
                                         RedissonClient redissonClient,
                                         KeyConverter<K> keyConverter,
                                         FieldConverter<F> fieldConverter,
                                         DynamicHashCacheLoader<K, F, V> cacheLoader,
                                         InvocationChainRegistry chainRegistry,
                                         CacheBroadcastPublisher broadcastPublisher) {
        this.config = config;
        this.cacheLoader = cacheLoader;
        this.broadcastPublisher = broadcastPublisher;
        this.componentName = componentNane;

        this.multiTierCache = new DefaultMultiTierDynamicHashCache<>(componentNane, config, redissonClient, keyConverter,
                fieldConverter);

        this.fullyLoadedTs = Caffeine.newBuilder()
                .expireAfterWrite(config.getSpec().getFullyLoadedExpireSecs(), TimeUnit.MINUTES)
                .maximumSize(100_000)
                .build();


//        PenetrationProtectConfig localPpConfig = config.getEnhance().getLocalPenetrationProtect();
//        CaffeinePenetrationProtector localProtector = CaffeinePenetrationProtector.of(localPpConfig.getPrefix()
//                , getComponentName(), localPpConfig.getTtlSecs(), localPpConfig.getMaxSize());
//        PenetrationProtectConfig remotePpConfig = config.getEnhance().getRemotePenetrationProtect();
//        RedisPenetrationProtector redisProtector = RedisPenetrationProtector.of(redissonClient
//                , remotePpConfig.getPrefix(), getComponentName(), remotePpConfig.getTtlSecs(),
//                remotePpConfig.getMaxSize());
//
//        PenetrationProtector penetrationProtector = new CompositePenetrationProtector(localProtector, redisProtector);
//        boolean allowNullValue = config.getSpec().getAllowNullValue() != null
//                ? config.getSpec().getAllowNullValue() : false;
//        this.interceptors.add(new MetricsInterceptor(registry));

//        if (CollUtil.isNotEmpty(interceptors)) {
//            this.interceptors.addAll(interceptors);
//        }
        this.chainRegistry = chainRegistry;
    }

    @Override
    public BaseSingleResult<V> get(K bizKey, F bizField) {
        DefaultInvocationContext ctx = new DefaultInvocationContext(componentName, "get", StructureType.DYNAMIC_HASH,
                BehaviorType.SINGLE_GET);
        return chainRegistry.getChain(StructureType.DYNAMIC_HASH, BehaviorType.SINGLE_GET).invoke(ctx);
//        return invoke("get", () -> doGet(bizKey, bizField), new CacheAccessKey(bizKey, bizField));
    }

    protected BaseSingleResult<V> doGet(K bizKey, F bizField) {
        try {
            BaseSingleResult<V> result = multiTierCache.get(bizKey, bizField);
            if (result.outcome() == CacheOutcome.HIT) {
                CacheValueHolder<V> holder = result.value();
                if (holder.isNotLogicExpired()) {
                    return BaseSingleResult.hit(componentName, holder, result.hitTier());
                }
            }

            // 回源加载数据
            V loaded = cacheLoader.load(bizKey, bizField);
            if (loaded == null) {
                return ResultFactory.notFoundSingle(componentName);
            }

            // 封装为缓存值并写入缓存
            CacheValueHolder<V> valueHolder = CacheValueHolder.wrap(loaded, config.getRemote().getTtlSecs());
            multiTierCache.put(bizKey, bizField, valueHolder);

            try {
                Map<F, V> loadedMap = Collections.singletonMap(bizField, loaded);
                ExecutableCommand command = ExecutableCommand.dynamicHash(componentName, CacheAgentMethod.PUT_ALL,
                        bizKey, loadedMap);
                broadcastPublisher.publish(command);
            } catch (Exception e) {
                log.warn("broadcast failed, agent = {}, key = {}, field = {}", componentName, bizKey, bizField, e);
            }

            return BaseSingleResult.hit(componentName, CacheValueHolder.wrap(loaded, 0),
                    HitTier.SOURCE);
        } catch (Exception e) {
            log.warn("cache load failed, agent = {}, key = {}, field = {}", componentName, bizKey, bizField, e);
            return BaseSingleResult.fail(componentName, e);
        } finally {
            CacheAccessContext.clear();
        }
    }

    @Override
    public BaseBatchResult<F, V> batchGet(K bizKey, List<F> bizFields) {
        return invoke("batchGet", () -> doBatchGet(bizKey, bizFields), CacheAccessKey.batch(bizKey, bizFields));
    }

    private BaseBatchResult<F, V> doBatchGet(K bizKey, List<F> bizFields) {
        Map<F, CacheValueHolder<V>> resultValueHolderMap = new HashMap<>();
        Map<F, HitTier> resultHitTierMap = new HashMap<>();

        try {
            // Step 1: 批量从缓存获取
            BaseBatchResult<F, V> cacheStorageResult = multiTierCache.batchGet(bizKey, bizFields);

            // Step 2: 识别需要回源的字段
            List<F> missedFields = new ArrayList<>();

            Map<F, CacheValueHolder<V>> cacheValueHolderMap = cacheStorageResult.value();
            Map<F, HitTier> cacheHitTierMap = cacheStorageResult.hitTierMap();
            if (CollUtil.isNotEmpty(cacheValueHolderMap)) {
                for (F bizField : cacheValueHolderMap.keySet()) {
                    CacheValueHolder<V> valueHolder = cacheValueHolderMap.get(bizField);
                    if (null != valueHolder && valueHolder.isNotLogicExpired()) {
                        resultValueHolderMap.put(bizField, valueHolder); // 命中直接返回
                        resultHitTierMap.put(bizField, cacheHitTierMap.get(bizField));
                    } else {
                        missedFields.add(bizField);      // miss 或过期，需回源
                    }
                }
            }

            // Step 3: 回源加载 + 回写缓存
            if (!missedFields.isEmpty()) {
                Map<F, V> loadedMap = cacheLoader.batchLoad(bizKey, missedFields);
                if (CollUtil.isNotEmpty(loadedMap)) {
                    for (F field : missedFields) {
                        V loaded = loadedMap.get(field);
                        if (loaded != null) {
                            resultValueHolderMap.put(field, CacheValueHolder.wrap(loaded, 0));
                            resultHitTierMap.put(field, HitTier.SOURCE);
                        }
                    }
                    multiTierCache.putAll(bizKey, loadedMap);
                }
            }
            return BaseBatchResult.hit(componentName, resultValueHolderMap, resultHitTierMap);
        } catch (Exception e) {
            log.warn("batchGet with fallback failed, agent = {}, key = {}, fields = {}", componentName, bizKey, bizFields, e);
            return BaseBatchResult.fail(componentName, e);
        } finally {
            CacheAccessContext.clear();
        }
    }

    //
//
//    @Override
//    public DynamicHashCacheAgentSingleAccessResult<K, F, V> listAll(K bizKey) {
//        boolean force = CacheAccessContext.isForceRefresh();
//        return invoke("listAll", () -> loadAllAndUpdate(bizKey, force), new CacheAccessKey(bizKey, null));
//    }
//
    @Override
    public BaseBatchResult<Void, Void> batchRefresh(K bizKey, List<F> bizFields) {
        return invoke("batchRefresh", () -> doBatchRefresh(bizKey, bizFields),
                CacheAccessKey.batch(bizKey, bizFields));
    }

    @Override
    public BaseBatchResult<Void, Void> invalidateFields(K bizKey, List<F> bizFields) {
        return null;
    }

    public BaseBatchResult<Void, Void> doInvalidateFields(K bizKey, List<F> bizFields) {
        try {
            multiTierCache.invalidateFields(bizKey, bizFields);
        } catch (Exception e) {
            log.warn("invalidateFields failed, agent = {}", componentName, e);
            return BaseBatchResult.fail(componentName, e);
        }
        return null;
    }

    public BaseBatchResult<Void, Void> doBatchRefresh(K bizKey, List<F> bizFields) {
        try {
            Map<F, V> loaded = cacheLoader.batchLoad(bizKey, bizFields);
            multiTierCache.putAll(bizKey, loaded);
            List<F> missedFields = bizFields.stream()
                    .filter(field -> !loaded.containsKey(field))
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(missedFields)) {
                for (Map.Entry<F, V> entry : loaded.entrySet()) {
                    multiTierCache.invalidate(bizKey, entry.getKey());
                }
            }
            return BaseBatchResult.success(componentName);
        } catch (Exception e) {
            log.warn("batchRefresh failed, agent = {}", componentName, e);
            return BaseBatchResult.fail(componentName, e);
        } finally {
            CacheAccessContext.clear();
        }
    }

    //
//    @Override
//    public DynamicHashCacheAgentSingleAccessResult<K, F, V> refreshAll(K bizKey) {
//        return invoke("refreshAll", () -> loadAllAndUpdate(bizKey, true));
//    }
//
//    private DynamicHashCacheAgentSingleAccessResult<K, F, V> loadAllAndUpdate(K bizKey, boolean force) {
//        try {
//            // ✅ 1. 非强制刷新时，优先使用结构新鲜期窗口逻辑
//            if (!force && withinFullyLoadedExpireWindow(bizKey)) {
//                log.debug("Fully-loaded freshness window active for key = {}", bizKey);
//                StorageCacheAccessResultBak<Map<F, CacheValueHolder<V>>> result = cache.listAll(bizKey);
//                if (result.outcome() == CacheOutcome.HIT) {
//                    return DynamicHashCacheAgentSingleAccessResult.success(
//                            getCacheName(), result.value(), result.getTier());
//                }
//            }
//
//            // ✅ 2. 回源加载数据
//            Map<F, V> loadedMap = cacheLoader.loadAll(bizKey);
//            if (loadedMap == null || loadedMap.isEmpty()) {
//                // ✅ 如果 source 明确为空，删除缓存，防止污染
//                cache.invalidateAll(bizKey);
//                fullyLoadedTs.invalidate(bizKey);
//                log.info("force refresh removed empty structure, agent = {}, key = {}", cacheName, bizKey);
//                return DynamicHashCacheAgentSingleAccessResult.dynamicHashNotFound(getCacheName());
//            }
//
//            // ✅ 3. 回源成功 → 缓存 + 更新结构级 fullyLoadedTs 标记
//            cache.putAll(bizKey, loadedMap);
//            fullyLoadedTs.put(bizKey, System.currentTimeMillis());
//
//            return DynamicHashCacheAgentSingleAccessResult.success(
//                    getCacheName(),
//                    CacheValueHolderHelper.wrapAsHolderMap(loadedMap),
//                    HitTier.SOURCE);
//
//        } catch (Exception e) {
//            log.warn("cache loadAll failed, agent = {}, key = {}", cacheName, bizKey, e);
//            return DynamicHashCacheAgentSingleAccessResult.dynamicHashFail(cacheName, e);
//        } finally {
//            CacheAccessContext.clear();
//        }
//    }
//
//    private boolean withinFullyLoadedExpireWindow(K bizKey) {
//        long expireSecs = config.getSpec().getFullyLoadedExpireSecs();
//        if (expireSecs <= 0) {
//            return false;
//        }
//
//        Long lastFullLoad = fullyLoadedTs.getIfPresent(bizKey);
//        return lastFullLoad != null &&
//                (System.currentTimeMillis() - lastFullLoad <= expireSecs * 1000L);
//    }
//
//
//    @Override
//    public DynamicHashCacheAgentSingleAccessResult<K, F, V> remove(K bizKey, F bizField) {
//        return invoke("invalidate", () -> doInvalidate(bizKey, bizField));
//    }
//
//    private DynamicHashCacheAgentSingleAccessResult<K, F, V> doInvalidate(K bizKey, F bizField) {
//        try {
//            cache.invalidate(bizKey, bizField);
//            return DynamicHashCacheAgentSingleAccessResult.success(getCacheName(),
//                    Collections.emptyMap(), HitTier.NONE); // 删除不关心命中层
//        } catch (Exception e) {
//            log.warn("invalidate failed, agent = {}, key = {}, field = {}", cacheName, bizKey, bizField, e);
//            return DynamicHashCacheAgentSingleAccessResult.dynamicHashFail(cacheName, e);
//        } finally {
//            CacheAccessContext.clear();
//        }
//    }
//
//    @Override
//    public DynamicHashCacheAgentSingleAccessResult<K, F, V> removeAll(K bizKey) {
//        return invoke("invalidateAll", () -> doInvalidateAll(bizKey));
//    }
//
//    @Override
//    public DynamicHashCacheAgentSingleAccessResult<K, F, V> put(K bizKey, F bizField, V value) {
//        return invoke("put", () -> doPut(bizKey, bizField, value), new CacheAccessKey(bizKey, bizField));
//    }
//
//    public DynamicHashCacheAgentSingleAccessResult<K, F, V> doPut(K bizKey, F bizField, V value) {
//        if (bizKey == null || bizField == null || value == null) {
//            return DynamicHashCacheAgentSingleAccessResult.badParam(cacheName);
//        }
//        try {
//            cache.put(bizKey, bizField, value);
//            return DynamicHashCacheAgentSingleAccessResult.success(cacheName);
//        } catch (Exception e) {
//            return DynamicHashCacheAgentSingleAccessResult.dynamicHashFail(cacheName, e);
//        }
//    }
//
    @Override
    public BaseBatchResult<Void, Void> putAll(K bizKey, Map<F, V> valueMap, Long version) {
        return invoke("putAll", () -> doPutAll(bizKey, valueMap, version),
                CacheAccessKey.batch(bizKey, new ArrayList<>(valueMap.keySet())));
    }

    public BaseBatchResult<Void, Void> doPutAll(K bizKey, Map<F, V> valueMap, Long version) {
        if (bizKey == null || CollUtil.isEmpty(valueMap)) {
            return ResultFactory.badParamBatch(componentName);
        }

        try {
            multiTierCache.putAll(bizKey, valueMap);
            return BaseBatchResult.success(componentName);
        } catch (Exception e) {
            log.warn("putAll failed, agent = {}, key = {}, fields = {}", componentName, bizKey, valueMap.keySet(), e);
            return BaseBatchResult.fail(componentName, e);
        } finally {
            CacheAccessContext.clear();
        }
    }
//}


//    private DynamicHashCacheAgentSingleAccessResult<K, F, V> doInvalidateAll(K bizKey) {
//        try {
//            multitierCache.invalidateAll(bizKey);
//            return DynamicHashCacheAgentSingleAccessResult.success(getCacheName(),
//                    Collections.emptyMap(), HitTier.NONE);
//        } catch (Exception e) {
//            log.warn("invalidateAll failed, agent = {}, key = {}", cacheName, bizKey, e);
//            return DynamicHashCacheAgentSingleAccessResult.dynamicHashFail(cacheName, e);
//        } finally {
//            CacheAccessContext.clear();
//        }
//    }

    @Override
    @SuppressWarnings("unchecked")
    protected BaseResult<Void> defaultFail(String method, Throwable t) {
        return ResultFactory.fail(componentName, t);
    }

    @Override
    public String componentName() {
        return this.componentName;
    }
}
