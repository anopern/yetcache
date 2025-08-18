package com.yetcache.agent.core.structure.hash;

import cn.hutool.core.collection.CollUtil;
import com.yetcache.agent.broadcast.InstanceIdProvider;
import com.yetcache.agent.broadcast.command.CacheShape;
import com.yetcache.agent.broadcast.command.CacheUpdateCommand;
import com.yetcache.agent.broadcast.command.CommandDescriptor;
import com.yetcache.agent.broadcast.command.playload.HashPlayload;
import com.yetcache.agent.broadcast.publisher.CacheBroadcastPublisher;
import com.yetcache.agent.core.PutAllOptions;
import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.port.HashCacheFillPort;
import com.yetcache.core.cache.*;
import com.yetcache.agent.core.structure.hash.batchget.HashCacheAgentBatchGetInvocationCommand;
import com.yetcache.agent.core.structure.hash.get.HashCacheAgentGetInvocationCommand;
import com.yetcache.agent.interceptor.*;
import com.yetcache.core.cache.command.HashCacheBatchRemoveCommand;
import com.yetcache.core.cache.command.HashCachePutAllCommand;
import com.yetcache.core.cache.command.HashCacheRemoveCommand;
import com.yetcache.core.cache.hash.DefaultMultiLevelHashCache;
import com.yetcache.core.cache.hash.MultiLevelHashCache;
import com.yetcache.core.codec.TypeDescriptor;
import com.yetcache.core.codec.JsonValueCodec;
import com.yetcache.core.codec.TypeRefRegistry;
import com.yetcache.core.config.CacheTier;
import com.yetcache.core.config.dynamichash.HashCacheConfig;
import com.yetcache.core.result.BaseCacheResult;
import com.yetcache.core.result.CacheResult;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.key.KeyConverter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
@Slf4j
@Getter
public class BaseHashCacheAgent implements HashCacheAgent {
    private final HashAgentScope scope;
    private final CacheInvocationChainRegistry chainRegistry;

    public BaseHashCacheAgent(String componentNane,
                              HashCacheConfig config,
                              RedissonClient redissonClient,
                              KeyConverter keyConverter,
                              FieldConverter fieldConverter,
                              HashCacheLoader cacheLoader,
                              CacheBroadcastPublisher broadcastPublisher,
                              CacheInvocationChainRegistry chainRegistry,
                              TypeRefRegistry typeRefRegistry,
                              TypeDescriptor typeDescriptor,
                              JsonValueCodec jsonValueCodec) {

        MultiLevelHashCache multiLevelCache = new DefaultMultiLevelHashCache(componentNane,
                config, redissonClient, keyConverter, fieldConverter, jsonValueCodec);

        HashCacheFillPort fillPort = (bizKey, valueMap) -> this.putAll(bizKey, valueMap, PutAllOptions.defaultOptions());
        this.scope = new HashAgentScope(componentNane,
                multiLevelCache,
                config,
                keyConverter,
                fieldConverter,
                cacheLoader,
                broadcastPublisher,
                fillPort,
                typeDescriptor);

        String typeId = TypeDescriptor.toTypeId(typeDescriptor.getValueTypeRef());
        if (null == typeRefRegistry.get(typeId)) {
            typeRefRegistry.register(TypeDescriptor.toTypeId(typeDescriptor.getValueTypeRef()),
                    typeDescriptor.getValueTypeRef());
        }

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
    @SuppressWarnings("unchecked")
    public <K, F, T> BaseCacheResult<T> get(K bizKey, F bizField) {
        StructureBehaviorKey structureBehaviorKey = StructureBehaviorKey.of(StructureType.DYNAMIC_HASH,
                BehaviorType.GET);
        CacheInvocationCommand command = new HashCacheAgentGetInvocationCommand(bizKey, bizField);
        return (BaseCacheResult<T>) singleInvoke(structureBehaviorKey, command);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, F, T> BaseCacheResult<T> batchGet(K bizKey, List<F> bizFields) {
        StructureBehaviorKey structureBehaviorKey = StructureBehaviorKey.of(StructureType.DYNAMIC_HASH,
                BehaviorType.BATCH_GET);
        List<Object> objBizFields = new ArrayList<>(bizFields);
        CacheInvocationCommand command = new HashCacheAgentBatchGetInvocationCommand(bizKey, objBizFields);
        return (BaseCacheResult<T>) batchInvoke(structureBehaviorKey, command);
    }

    @Override
    public <K, F> BaseCacheResult<Void> refresh(K bizKey, F bizField) {
        if (null == bizKey || null == bizField) {
            return BaseCacheResult.fail(scope.getComponentName(), new IllegalArgumentException("bizKey or bizField is null"));
        }
        CacheResult loadRet = scope.getCacheLoader().load(HashCacheLoadCommand.of(bizKey, bizField));
        if (!loadRet.isSuccess()) {
            return BaseCacheResult.fail(scope.getComponentName(), loadRet.errorInfo());
        }
        if (null != loadRet.value()) {
            HashCachePutAllCommand putCmd = HashCachePutAllCommand.builder()
                    .bizKey(bizKey)
                    .valueMap(Collections.singletonMap(bizField, loadRet.value()))
                    .ttl(CacheTtl.builder()
                            .localLogicSecs(scope.getConfig().getLocal().getLogicTtlSecs())
                            .localPhysicalSecs(scope.getConfig().getLocal().getPhysicalTtlSecs())
                            .remoteLogicSecs(scope.getConfig().getRemote().getLogicTtlSecs())
                            .remotePhysicalSecs(scope.getConfig().getRemote().getPhysicalTtlSecs())
                            .build())
                    .writeTier(WriteTier.ALL)
                    .build();
            scope.getMultiLevelCache().putAll(putCmd);
        } else {
            HashCacheRemoveCommand rmCmd = HashCacheRemoveCommand.of(bizKey, bizField);
            scope.getMultiLevelCache().remove(rmCmd);
        }
        return BaseCacheResult.success(scope.getComponentName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, F, T> BaseCacheResult<Void> batchRefresh(K bizKey, List<F> bizFields) {
        if (null == bizKey || CollUtil.isEmpty(bizFields)) {
            return BaseCacheResult.fail(scope.getComponentName(), new IllegalArgumentException("bizKey or bizField is null"));
        }
        CacheResult loadRet = scope.getCacheLoader().batchLoad(HashCacheBatchLoadCommand.of(bizKey, bizFields));
        if (!loadRet.isSuccess()) {
            return BaseCacheResult.fail(scope.getComponentName(), loadRet.errorInfo());
        }

        List<F> missedFields = new ArrayList<>();
        if (null != loadRet.value()) {
            Map<Object, Object> valueMap = (Map<Object, Object>) loadRet.value();
            for (F field : bizFields) {
                if (!valueMap.containsKey(field)) {
                    missedFields.add(field);
                }
            }
            if (CollUtil.isNotEmpty(valueMap)) {
                putAll(bizKey, valueMap);
            }
        } else {
            HashCacheRemoveCommand rmCmd = HashCacheRemoveCommand.of(bizKey, missedFields);
            scope.getMultiLevelCache().remove(rmCmd);
        }
        return BaseCacheResult.success(scope.getComponentName());
    }

    @Override
    public <K, F> BaseCacheResult<Void> remove(K bizKey, F bizField) {
        HashCacheRemoveCommand cmd = HashCacheRemoveCommand.of(bizKey, bizField);
        scope.getMultiLevelCache().remove(cmd);
        return BaseCacheResult.success(scope.getComponentName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, F> BaseCacheResult<Void> batchRemove(K bizKey, List<F> bizFields) {
        HashCacheBatchRemoveCommand cmd = HashCacheBatchRemoveCommand.of(bizKey, new ArrayList<>(bizFields));
        return (BaseCacheResult<Void>) scope.getMultiLevelCache().batchRemove(cmd);
    }

    private CacheResult singleInvoke(StructureBehaviorKey structureBehaviorKey, CacheInvocationCommand command) {
        CacheInvocationContext ctx = new CacheInvocationContext(command, scope);
        try {
            CacheInvocationChain chain = chainRegistry.getChain(structureBehaviorKey);
            CacheResult rawResult = chain.proceed(ctx);
            return BaseCacheResult.singleHit(scope.getComponentName(), rawResult.value(), rawResult.hitTierInfo().hitTier());
        } catch (Throwable e) {
            return BaseCacheResult.fail(scope.getComponentName(), e);
        }
    }

    private CacheResult batchInvoke(StructureBehaviorKey structureBehaviorKey, CacheInvocationCommand command) {
        CacheInvocationContext ctx = new CacheInvocationContext(command, scope);
        try {
            CacheInvocationChain chain = chainRegistry.getChain(structureBehaviorKey);
            CacheResult rawResult = chain.proceed(ctx);
            return BaseCacheResult.batchHit(scope.getComponentName(), rawResult.value(), rawResult.hitTierInfo());
        } catch (Throwable e) {
            return BaseCacheResult.fail(scope.getComponentName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <K> BaseCacheResult<Void> putAll(K bizKey, Map<Object, Object> valueMap) {
        HashCachePutAllCommand putCmd = HashCachePutAllCommand.builder()
                .bizKey(bizKey)
                .valueMap(valueMap)
                .ttl(CacheTtl.builder()
                        .localLogicSecs(scope.getConfig().getLocal().getLogicTtlSecs())
                        .localPhysicalSecs(scope.getConfig().getLocal().getPhysicalTtlSecs())
                        .remoteLogicSecs(scope.getConfig().getRemote().getLogicTtlSecs())
                        .remotePhysicalSecs(scope.getConfig().getRemote().getPhysicalTtlSecs())
                        .build())
                .writeTier(WriteTier.ALL)
                .build();
        return (BaseCacheResult<Void>) scope.getMultiLevelCache().putAll(putCmd);
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
//                StorageCacheAccessResultBak<Map<F, CacheValueHolder>> result = cache.listAll(bizKey);
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
    public <K, F, T> CacheResult putAll(K bizKey, Map<F, T> valueMap, PutAllOptions opts) {
        log.debug("缓存代理类 {} 缓存更新 bizKey: {}, valueMap: {}, opts: {}", scope.getComponentName(), bizKey,
                valueMap, opts);
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

        CacheResult writeResult = this.scope.getMultiLevelCache().putAll(putAllCmd);

        // 3) 本实例 local 广播（仅在写入成功且开启时）
        if (writeResult.isSuccess() && normalized.isBroadcast()) {
            try {
                CacheUpdateCommand broadcastCmd = CacheUpdateCommand.builder()
                        .descriptor(CommandDescriptor.builder()
                                .shape(CacheShape.HASH.getName())
                                .componentName(scope.getComponentName())
                                .structureBehaviorKey(StructureBehaviorKey.of(StructureType.DYNAMIC_HASH, BehaviorType.PUT_ALL))
                                .instanceId(InstanceIdProvider.getInstanceId())
                                .publishAt(System.currentTimeMillis())
                                .build())
                        .payload(HashPlayload.builder()
                                .valueTypeId(scope.getTypeDescriptor().getValueTypeId())
                                .key(scope.getKeyConverter().convert(bizKey))
                                .fieldValues(valueMap.entrySet().stream()
                                        .map(entry -> HashPlayload.FieldValue.of(scope.getFieldConverter()
                                                .convert(entry.getKey()), entry.getValue()))
                                        .collect(Collectors.toList())).build())
                        .build();
                this.scope.getBroadcastPublisher().publish(broadcastCmd);
            } catch (Exception e) {
                // 不让广播失败影响主流程；按需记录日志/埋点
                log.warn("broadcastPutAll(local) failed, bizKey={}, cause={}", bizKey, e.toString());
            }
        }

        return writeResult;
    }

    @Override
    public <K, F, T> CacheResult putAllToLocal(String key, Map<String, T> valueMap) {
        K bizKey = scope.getKeyConverter().revert(key);
        Map<F, T> typedValueMap = valueMap.entrySet().stream()
                .collect(Collectors.toMap(entry -> scope.getFieldConverter().revert(entry.getKey()),
                        Map.Entry::getValue));
        return putAll(bizKey, typedValueMap, PutAllOptions.builder().broadcast(false).build());
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

    public CacheTier cacheTier() {
        return scope.getConfig().getSpec().getCacheTier();
    }

    @Override
    public String componentName() {
        return this.scope.getComponentName();
    }
}
