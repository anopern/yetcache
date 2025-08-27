//package com.yetcache.agent.core.structure.hash;
//
//import cn.hutool.core.collection.CollUtil;
//import com.yetcache.agent.broadcast.InstanceIdProvider;
//import com.yetcache.agent.broadcast.command.CacheShape;
//import com.yetcache.agent.broadcast.command.CacheCommand;
//import com.yetcache.agent.broadcast.command.CommandDescriptor;
//import com.yetcache.agent.broadcast.command.playload.HashPlayload;
//import com.yetcache.agent.broadcast.publisher.CacheBroadcastPublisher;
//import com.yetcache.agent.core.PutAllOptions;
//import com.yetcache.agent.core.StructureType;
//import com.yetcache.agent.core.port.HashCacheFillPort;
//import com.yetcache.core.cache.*;
//import com.yetcache.agent.core.structure.hash.batchget.HashCacheAgentBatchGetInvocationCommand;
//import com.yetcache.agent.core.structure.hash.get.HashCacheAgentGetInvocationCommand;
//import com.yetcache.agent.interceptor.*;
//import com.yetcache.core.cache.command.hash.HashCacheBatchRemoveCommand;
//import com.yetcache.core.cache.command.hash.HashCachePutAllCommand;
//import com.yetcache.core.cache.command.hash.HashCacheRemoveCommand;
//import com.yetcache.core.cache.hash.DefaultMultiLevelHashCache;
//import com.yetcache.core.cache.hash.MultiLevelHashCache;
//import com.yetcache.core.codec.TypeDescriptor;
//import com.yetcache.core.codec.JsonValueCodec;
//import com.yetcache.core.codec.TypeRefRegistry;
//import com.yetcache.core.config.CacheLevel;
//import com.yetcache.core.config.hash.HashCacheConfig;
//import com.yetcache.core.result.BaseCacheResult;
//import com.yetcache.core.result.CacheResult;
//import com.yetcache.core.support.field.FieldConverter;
//import com.yetcache.core.support.key.KeyConverter;
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import org.redisson.api.RedissonClient;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * @author walter.yan
// * @since 2025/7/14
// */
//@Slf4j
//@Getter
//public class BaseHashCacheAgent implements HashCacheAgent {
//    private final HashAgentScope scope;
//    private final CacheInvocationChainRegistry chainRegistry;
//
//    public BaseHashCacheAgent(String componentNane,
//                              HashCacheConfig config,
//                              RedissonClient redissonClient,
//                              KeyConverter keyConverter,
//                              FieldConverter fieldConverter,
//                              HashCacheLoader cacheLoader,
//                              CacheBroadcastPublisher broadcastPublisher,
//                              CacheInvocationChainRegistry chainRegistry,
//                              TypeRefRegistry typeRefRegistry,
//                              TypeDescriptor typeDescriptor,
//                              JsonValueCodec jsonValueCodec) {
//
//        MultiLevelHashCache multiLevelCache = new DefaultMultiLevelHashCache(componentNane,
//                config, redissonClient, keyConverter, fieldConverter, jsonValueCodec);
//
//        HashCacheFillPort fillPort = (bizKey, valueMap) -> this.putAll(bizKey, valueMap, PutAllOptions.defaultOptions());
//        this.scope = new HashAgentScope(componentNane,
//                multiLevelCache,
//                config,
//                keyConverter,
//                fieldConverter,
//                cacheLoader,
//                broadcastPublisher,
//                fillPort,
//                typeDescriptor);
//
//        String typeId = TypeDescriptor.toTypeId(typeDescriptor.getValueTypeRef());
//        if (null == typeRefRegistry.get(typeId)) {
//            typeRefRegistry.register(TypeDescriptor.toTypeId(typeDescriptor.getValueTypeRef()),
//                    typeDescriptor.getValueTypeRef());
//        }
//
////        PenetrationProtectConfig localPpConfig = config.getEnhance().getLocalPenetrationProtect();
////        CaffeinePenetrationProtector localProtector = CaffeinePenetrationProtector.of(localPpConfig.getPrefix()
////                , getComponentName(), localPpConfig.getTtlSecs(), localPpConfig.getMaxSize());
////        PenetrationProtectConfig remotePpConfig = config.getEnhance().getRemotePenetrationProtect();
////        RedisPenetrationProtector redisProtector = RedisPenetrationProtector.of(redissonClient
////                , remotePpConfig.getPrefix(), getComponentName(), remotePpConfig.getTtlSecs(),
////                remotePpConfig.getMaxSize());
////
////        PenetrationProtector penetrationProtector = new CompositePenetrationProtector(localProtector, redisProtector);
////        boolean allowNullValue = config.getSpec().getAllowNullValue() != null
////                ? config.getSpec().getAllowNullValue() : false;
////        this.interceptors.add(new MetricsInterceptor(registry));
//
////        if (CollUtil.isNotEmpty(interceptors)) {
////            this.interceptors.addAll(interceptors);
////        }
//        this.chainRegistry = chainRegistry;
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public <K, F, T> BaseCacheResult<T> get(K bizKey, F bizField) {
//        StructureBehaviorKey structureBehaviorKey = StructureBehaviorKey.of(StructureType.HASH,
//                BehaviorType.GET);
//        CacheInvocationCommand cmd = HashCacheAgentGetInvocationCommand.of(scope.getCacheAgentName(), bizKey, bizField);
//        return (BaseCacheResult<T>) singleInvoke(structureBehaviorKey, cmd);
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public <K, F, T> BaseCacheResult<T> batchGet(K bizKey, List<F> bizFields) {
//        StructureBehaviorKey structureBehaviorKey = StructureBehaviorKey.of(StructureType.HASH,
//                BehaviorType.BATCH_GET);
//        CacheInvocationCommand cmd = HashCacheAgentBatchGetInvocationCommand.of(scope.getCacheAgentName(), bizKey,
//                new ArrayList<>(bizFields));
//        return (BaseCacheResult<T>) batchInvoke(structureBehaviorKey, cmd);
//    }
//
//    @Override
//    public <K, F> BaseCacheResult<Void> refresh(K bizKey, F bizField) {
//        if (null == bizKey || null == bizField) {
//            return BaseCacheResult.fail(scope.getCacheAgentName(), new IllegalArgumentException("bizKey or bizField is null"));
//        }
//        CacheResult loadRet = scope.getCacheLoader().load(HashCacheLoadCommand.of(bizKey, bizField));
//        if (!loadRet.isSuccess()) {
//            return BaseCacheResult.fail(scope.getCacheAgentName(), loadRet.errorInfo());
//        }
//        if (null != loadRet.value()) {
//            HashCachePutAllCommand putCmd = HashCachePutAllCommand.builder()
//                    .bizKey(bizKey)
//                    .valueMap(Collections.singletonMap(bizField, loadRet.value()))
//                    .ttl(CacheTtl.builder()
//                            .localLogicSecs(scope.getConfig().getLocal().getLogicTtlSecs())
//                            .localPhysicalSecs(scope.getConfig().getLocal().getPhysicalTtlSecs())
//                            .remoteLogicSecs(scope.getConfig().getRemote().getLogicTtlSecs())
//                            .remotePhysicalSecs(scope.getConfig().getRemote().getPhysicalTtlSecs())
//                            .build())
//                    .writeLevel(WriteLevel.ALL)
//                    .build();
//            scope.getMultiLevelCache().putAll(putCmd);
//        } else {
//            HashCacheRemoveCommand rmCmd = HashCacheRemoveCommand.of(bizKey, bizField);
//            scope.getMultiLevelCache().remove(rmCmd);
//        }
//        return BaseCacheResult.success(scope.getCacheAgentName());
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public <K, F, T> BaseCacheResult<Void> batchRefresh(K bizKey, List<F> bizFields) {
//        if (null == bizKey || CollUtil.isEmpty(bizFields)) {
//            return BaseCacheResult.fail(scope.getCacheAgentName(), new IllegalArgumentException("bizKey or bizField is null"));
//        }
//        CacheResult loadRet = scope.getCacheLoader().batchLoad(HashCacheBatchLoadCommand.of(bizKey, bizFields));
//        if (!loadRet.isSuccess()) {
//            return BaseCacheResult.fail(scope.getCacheAgentName(), loadRet.errorInfo());
//        }
//
//        List<F> missedFields = new ArrayList<>();
//        if (null != loadRet.value()) {
//            Map<Object, Object> valueMap = (Map<Object, Object>) loadRet.value();
//            for (F field : bizFields) {
//                if (!valueMap.containsKey(field)) {
//                    missedFields.add(field);
//                }
//            }
//            if (CollUtil.isNotEmpty(valueMap)) {
//                putAll(bizKey, valueMap);
//            }
//        } else {
//            HashCacheRemoveCommand rmCmd = HashCacheRemoveCommand.of(bizKey, missedFields);
//            scope.getMultiLevelCache().remove(rmCmd);
//        }
//        return BaseCacheResult.success(scope.getCacheAgentName());
//    }
//
//    @Override
//    public <K, F> BaseCacheResult<Void> remove(K bizKey, F bizField) {
//        HashCacheRemoveCommand cmd = HashCacheRemoveCommand.of(bizKey, bizField);
//        scope.getMultiLevelCache().remove(cmd);
//        return BaseCacheResult.success(scope.getCacheAgentName());
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public <K, F> BaseCacheResult<Void> batchRemove(K bizKey, List<F> bizFields) {
//        HashCacheBatchRemoveCommand cmd = HashCacheBatchRemoveCommand.of(bizKey, new ArrayList<>(bizFields));
//        return (BaseCacheResult<Void>) scope.getMultiLevelCache().batchRemove(cmd);
//    }
//
//    private CacheResult singleInvoke(StructureBehaviorKey structureBehaviorKey, CacheInvocationCommand command) {
//        CacheInvocationContext ctx = new CacheInvocationContext(command, scope);
//        try {
//            CacheInvocationChain chain = chainRegistry.getChain(structureBehaviorKey);
//            CacheResult rawResult = chain.proceed(ctx);
//            return BaseCacheResult.singleHit(scope.getCacheAgentName(), rawResult.value(), rawResult.hitLevelInfo().hitLevel());
//        } catch (Throwable e) {
//            return BaseCacheResult.fail(scope.getCacheAgentName(), e);
//        }
//    }
//
//    private CacheResult batchInvoke(StructureBehaviorKey structureBehaviorKey, CacheInvocationCommand command) {
//        CacheInvocationContext ctx = new CacheInvocationContext(command, scope);
//        try {
//            CacheInvocationChain chain = chainRegistry.getChain(structureBehaviorKey);
//            CacheResult rawResult = chain.proceed(ctx);
//            return BaseCacheResult.batchHit(scope.getCacheAgentName(), rawResult.value(), rawResult.hitLevelInfo());
//        } catch (Throwable e) {
//            return BaseCacheResult.fail(scope.getCacheAgentName(), e);
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    private <K> BaseCacheResult<Void> putAll(K bizKey, Map<Object, Object> valueMap) {
//        HashCachePutAllCommand putCmd = HashCachePutAllCommand.builder()
//                .bizKey(bizKey)
//                .valueMap(valueMap)
//                .ttl(CacheTtl.builder()
//                        .localLogicSecs(scope.getConfig().getLocal().getLogicTtlSecs())
//                        .localPhysicalSecs(scope.getConfig().getLocal().getPhysicalTtlSecs())
//                        .remoteLogicSecs(scope.getConfig().getRemote().getLogicTtlSecs())
//                        .remotePhysicalSecs(scope.getConfig().getRemote().getPhysicalTtlSecs())
//                        .build())
//                .writeLevel(WriteLevel.ALL)
//                .build();
//        return (BaseCacheResult<Void>) scope.getMultiLevelCache().putAll(putCmd);
//    }
//
//    @Override
//    public <K, F, T> CacheResult putAll(K bizKey, Map<F, T> valueMap, PutAllOptions opts) {
//        log.debug("缓存代理类 {} 缓存更新 bizKey: {}, valueMap: {}, opts: {}", scope.getCacheAgentName(), bizKey,
//                valueMap, opts);
//        // 0) 入参校验
//        if (bizKey == null) {
//            throw new IllegalArgumentException("bizKey is null");
//        }
//        if (valueMap == null || valueMap.isEmpty()) {
//            throw new IllegalArgumentException("valueMap is empty");
//        }
//
//        // 1) 归一化选项：null -> 默认广播到本实例 local
//        final PutAllOptions normalized = (opts != null) ? opts : PutAllOptions.defaultOptions();
//
//        // 2) 写入缓存（按你已有的默认策略执行：ALL 层/默认 TTL）
//        //    这里保留拷贝，避免调用方后续修改 valueMap 影响到存储/广播
//        Map<Object, Object> safeMap = Collections.unmodifiableMap(new LinkedHashMap<>(valueMap));
//
//        // Step 3: 回写缓存
//        HashCachePutAllCommand putAllCmd = HashCachePutAllCommand.builder()
//                .bizKey(bizKey)
//                .valueMap(safeMap)
//                .ttl(CacheTtl.builder()
//                        .localLogicSecs(scope.getConfig().getLocal().getLogicTtlSecs())
//                        .localPhysicalSecs(scope.getConfig().getLocal().getPhysicalTtlSecs())
//                        .remoteLogicSecs(scope.getConfig().getRemote().getLogicTtlSecs())
//                        .remotePhysicalSecs(scope.getConfig().getRemote().getPhysicalTtlSecs())
//                        .build())
//                .writeLevel(WriteLevel.ALL)
//                .build();
//
//        CacheResult writeResult = this.scope.getMultiLevelCache().putAll(putAllCmd);
//
//        // 3) 本实例 local 广播（仅在写入成功且开启时）
//        if (writeResult.isSuccess() && normalized.isBroadcast()) {
//            try {
//                CacheCommand broadcastCmd = CacheCommand.builder()
//                        .descriptor(CommandDescriptor.builder()
//                                .shape(CacheShape.HASH.getName())
//                                .cacheAgentName(scope.getCacheAgentName())
//                                .sbKey(StructureBehaviorKey.of(StructureType.HASH, BehaviorType.PUT_ALL))
//                                .instanceId(InstanceIdProvider.getInstanceId())
//                                .publishAt(System.currentTimeMillis())
//                                .build())
//                        .payload(HashPlayload.builder()
//                                .valueTypeId(scope.getTypeDescriptor().getValueTypeId())
//                                .key(scope.getKeyConverter().convert(bizKey))
//                                .fieldValues(valueMap.entrySet().stream()
//                                        .map(entry -> HashPlayload.FieldValue.of(scope.getFieldConverter()
//                                                .convert(entry.getKey()), entry.getValue()))
//                                        .collect(Collectors.toList())).build())
//                        .build();
//                this.scope.getBroadcastPublisher().publish(broadcastCmd);
//            } catch (Exception e) {
//                log.warn("broadcastPutAll(local) failed, bizKey={}, cause={}", bizKey, e.toString());
//            }
//        }
//
//        return writeResult;
//    }
//
//    @Override
//    public <K, F, T> CacheResult putAllToLocal(String key, Map<String, T> valueMap) {
//        K bizKey = scope.getKeyConverter().revert(key);
//        Map<F, T> typedValueMap = valueMap.entrySet().stream()
//                .collect(Collectors.toMap(entry -> scope.getFieldConverter().revert(entry.getKey()),
//                        Map.Entry::getValue));
//        return putAll(bizKey, typedValueMap, PutAllOptions.builder().broadcast(false).build());
//    }
//
//    public CacheLevel cacheLevel() {
//        return scope.getConfig().getSpec().getCacheLevel();
//    }
//
//    @Override
//    public String cacheAgentName() {
//        return this.scope.getCacheAgentName();
//    }
//}
