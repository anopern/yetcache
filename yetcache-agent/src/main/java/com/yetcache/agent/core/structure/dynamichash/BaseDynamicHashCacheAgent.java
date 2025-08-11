package com.yetcache.agent.core.structure.dynamichash;

import cn.hutool.core.collection.CollUtil;
import com.yetcache.agent.core.PutAllOptions;
import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.structure.dynamichash.batchget.DynamicHashCacheAgentBatchGetInvocationCommand;
import com.yetcache.agent.core.structure.dynamichash.get.DynamicHashCacheAgentGetInvocationCommand;
import com.yetcache.agent.interceptor.*;
import com.yetcache.core.cache.CacheTtl;
import com.yetcache.core.cache.WriteTier;
import com.yetcache.core.cache.command.HashCachePutAllCommand;
import com.yetcache.core.cache.dynamichash.DefaultMultiTierDynamicHashCache;
import com.yetcache.core.cache.dynamichash.MultiTierDynamicHashCache;
import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.context.CacheAccessContext;
import com.yetcache.core.result.CacheResult;
import com.yetcache.core.result.SingleCacheResult;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.K;
import org.redisson.api.RedissonClient;

import java.util.*;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
@Slf4j
public class BaseDynamicHashCacheAgent implements DynamicHashCacheAgent {
    private final DynamicHashAgentScope scope;
    private final CacheInvocationChainRegistry chainRegistry;

    public BaseDynamicHashCacheAgent(String componentNane,
                                     DynamicHashCacheConfig config,
                                     RedissonClient redissonClient,
                                     KeyConverter keyConverter,
                                     FieldConverter fieldConverter,
                                     DynamicHashCacheLoader cacheLoader,
                                     CacheInvocationChainRegistry chainRegistry) {

        MultiTierDynamicHashCache multiTierCache = new DefaultMultiTierDynamicHashCache<>(componentNane,
                config, redissonClient, keyConverter, fieldConverter);

        this.scope = new DynamicHashAgentScope(componentNane,
                multiTierCache,
                config,
                cacheLoader);


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
    public CacheResult get(Object bizKey, Object bizField) {
        StructureBehaviorKey structureBehaviorKey = StructureBehaviorKey.of(StructureType.DYNAMIC_HASH,
                BehaviorType.SINGLE_GET);
        CacheInvocationCommand command = new DynamicHashCacheAgentGetInvocationCommand(bizKey, bizField);
        return invoke(structureBehaviorKey, command);
    }

    @Override
    public CacheResult batchGet(Object bizKey, List<Object> bizFields) {
        StructureBehaviorKey structureBehaviorKey = StructureBehaviorKey.of(StructureType.DYNAMIC_HASH,
                BehaviorType.BATCH_GET);
        CacheInvocationCommand command = new DynamicHashCacheAgentBatchGetInvocationCommand(bizKey, bizFields);
        return invoke(structureBehaviorKey, command);
    }

    private CacheResult invoke(StructureBehaviorKey structureBehaviorKey, CacheInvocationCommand command) {
        CacheInvocationContext ctx = new CacheInvocationContext(command, scope);
        try {
            CacheInvocationChain chain = chainRegistry.getChain(structureBehaviorKey);
            CacheResult rawResult = chain.proceed(ctx);
            // ✅ 统一构造泛型结构体返回（仍然声明为 CacheResult）
            return SingleCacheResult.hit(scope.getComponentName(), rawResult.value(), rawResult.hitTierInfo().hitTier());
        } catch (Throwable e) {
            return SingleCacheResult.fail(scope.getComponentName(), e);
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
//    @Override
//    public BaseBatchResult<Void, Void> batchRefresh(K bizKey, List<F> bizFields) {
//        return invoke("batchRefresh", () -> doBatchRefresh(bizKey, bizFields),
//                CacheAccessKey.batch(bizKey, bizFields));
//    }

//    @Override
//    public BaseBatchResult<Void, Void> invalidateFields(K bizKey, List<F> bizFields) {
//        return null;
//    }

//    public BaseBatchResult<Void, Void> doInvalidateFields(K bizKey, List<F> bizFields) {
//        try {
//            multiTierCache.invalidateFields(bizKey, bizFields);
//        } catch (Exception e) {
//            log.warn("invalidateFields failed, agent = {}", componentName, e);
//            return BaseBatchResult.fail(componentName, e);
//        }
//        return null;
//    }

//    public BaseBatchResult<Void, Void> doBatchRefresh(K bizKey, List<F> bizFields) {
//        try {
//            Map<F, V> loaded = cacheLoader.batchLoad(bizKey, bizFields);
//            multiTierCache.putAll(bizKey, loaded);
//            List<F> missedFields = bizFields.stream()
//                    .filter(field -> !loaded.containsKey(field))
//                    .collect(Collectors.toList());
//            if (CollUtil.isEmpty(missedFields)) {
//                for (Map.Entry<F, V> entry : loaded.entrySet()) {
//                    multiTierCache.invalidate(bizKey, entry.getKey());
//                }
//            }
//            return BaseBatchResult.success(componentName);
//        } catch (Exception e) {
//            log.warn("batchRefresh failed, agent = {}", componentName, e);
//            return BaseBatchResult.fail(componentName, e);
//        } finally {
//            CacheAccessContext.clear();
//        }
//    }

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
    public CacheResult putAll(Object bizKey, Map<Object, Object> valueMap, PutAllOptions opts) {
        // 0) 入参校验
        if (bizKey == null) {
            throw new IllegalArgumentException("bizKey is null");
        }
        if (valueMap == null || valueMap.isEmpty()) {
            throw new IllegalArgumentException("valueMap is empty");
        }

        // 1) 归一化选项：null -> 默认广播到本实例 local
        final PutAllOptions normalized = (opts != null) ? opts : PutAllOptions.defaultOptions();

        // 2) 写入缓存（按你已有的默认策略执行：ALL 层/默认 TTL）
        //    这里保留拷贝，避免调用方后续修改 valueMap 影响到存储/广播
        Map<Object, Object> safeMap = Collections.unmodifiableMap(new LinkedHashMap<>(valueMap));

        // Step 3: 回写缓存
        HashCachePutAllCommand putAllCmd = HashCachePutAllCommand.builder()
                .bizKey(bizKey)
                .valueMap(safeMap)
                .ttl(CacheTtl.builder()
                        .localLogicSecs(scope.getConfig().getLocal().getLogicTtlSecs())
                        .localPhysicalSecs(scope.getConfig().getLocal().getPhysicalTtlSecs())
                        .remoteLogicSecs(scope.getConfig().getRemote().getLogicTtlSecs())
                        .remotePhysicalSecs(scope.getConfig().getRemote().getPhysicalTtlSecs())
                        .build())
                .writeTier(WriteTier.ALL)
                .build();

        CacheResult writeResult = this.scope.getMultiTierCache().putAll(putAllCmd);

        // 3) 本实例 local 广播（仅在写入成功且开启时）
        if (writeResult.isSuccess() && normalized.isBroadcast()) {
            try {
//                this.localBroadcaster.broadcastPutAll(bizKey, safeMap);
            } catch (Exception e) {
                // 不让广播失败影响主流程；按需记录日志/埋点
                log.warn("broadcastPutAll(local) failed, bizKey={}, cause={}", bizKey, e.toString());
            }
        }

        return writeResult;
    }


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

//    @Override
//    @SuppressWarnings("unchecked")
//    protected BaseResult<Void> defaultFail(String method, Throwable t) {
//        return ResultFactory.fail(componentName, t);
//    }

    @Override
    public String componentName() {
        return this.scope.getComponentName();
    }
}
