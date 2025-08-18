package com.yetcache.agent.core.structure.hash.batchget;

import cn.hutool.core.collection.CollUtil;
import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.structure.hash.HashAgentScope;
import com.yetcache.agent.core.structure.hash.HashCacheBatchLoadCommand;
import com.yetcache.agent.interceptor.*;
import com.yetcache.core.cache.CacheTtl;
import com.yetcache.core.cache.WriteTier;
import com.yetcache.core.cache.command.HashCacheBatchGetCommand;
import com.yetcache.core.cache.command.HashCachePutAllCommand;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.codec.TypeRef;
import com.yetcache.core.result.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Slf4j
public class HashCacheBatchGetInterceptor implements CacheInterceptor {

    @Override
    public String id() {
        return "hash-cache-batch-get-agent-interceptor";
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean supportStructureBehaviorKey(StructureBehaviorKey sbKey) {
        return StructureType.HASH.equals(sbKey.getStructureType())
                && BehaviorType.BATCH_GET.equals(sbKey.getBehaviorType());
    }


    @Override
    @SuppressWarnings("unchecked")
    public CacheResult invoke(CacheInvocationContext ctx, ChainRunner runner) throws Throwable {
        HashCacheAgentBatchGetInvocationCommand cmd =
                (HashCacheAgentBatchGetInvocationCommand) ctx.getCommand();
        Object bizKey = cmd.getBizKey();
        List<Object> bizFields = cmd.getBizFields();
        HashAgentScope agentScope = (HashAgentScope) ctx.getAgentScope();
        Map<Object, CacheValueHolder<?>> resultValueHolderMap = new HashMap<>();
        Map<Object, HitTier> resultHitTierMap = new HashMap<>();

        try {
            // Step 1: 读缓存
            TypeRef<?> valueTypeRef = agentScope.getTypeDescriptor().getValueTypeRef();
            HashCacheBatchGetCommand batchGetCmd = new HashCacheBatchGetCommand(bizKey, bizFields, valueTypeRef);
            CacheResult storeResult = agentScope.getMultiLevelCache().batchGet(batchGetCmd);

            List<Object> missedFields = new ArrayList<>();
            Map<Object, CacheValueHolder<?>> cacheValueHolderMap = (Map<Object, CacheValueHolder<?>>) storeResult.value();
            Map<Object, HitTier> cacheHitTierMap = storeResult.hitTierInfo().hitTierMap();

            for (Object field : bizFields) {
                CacheValueHolder<?> holder = cacheValueHolderMap.get(field);
                if (holder != null && holder.isNotLogicExpired()) {
                    resultValueHolderMap.put(field, holder);
                    resultHitTierMap.put(field, cacheHitTierMap.get(field));
                } else {
                    missedFields.add(field);
                }
            }

            // Step 2: 回源
            if (!missedFields.isEmpty()) {
                HashCacheBatchLoadCommand<?, ?> loadCmd = new HashCacheBatchLoadCommand<>(bizKey, missedFields);
                BaseCacheResult<Map<Object, Object>> loadResult = (BaseCacheResult<Map<Object, Object>>)
                        agentScope.getCacheLoader().batchLoad(loadCmd);
                if (!loadResult.isSuccess()) {
                    throw new RuntimeException("batchLoad failed. key=" + bizKey + ", fields=" + missedFields);
                }
                Map<Object, Object> loadedMap = loadResult.value();
                if (CollUtil.isNotEmpty(loadedMap)) {
                    for (Object field : missedFields) {
                        Object loaded = loadedMap.get(field);
                        if (loaded != null) {
                            CacheValueHolder<?> holder = CacheValueHolder.wrap(loaded, 0);
                            resultValueHolderMap.put(field, holder);
                            resultHitTierMap.put(field, HitTier.SOURCE);
                        }
                    }
                    // Step 3: 回写缓存
                    HashCachePutAllCommand putAllCmd = HashCachePutAllCommand.builder()
                            .bizKey(bizKey)
                            .valueMap(loadedMap)
                            .ttl(CacheTtl.builder()
                                    .localLogicSecs(agentScope.getConfig().getLocal().getLogicTtlSecs())
                                    .localPhysicalSecs(agentScope.getConfig().getLocal().getPhysicalTtlSecs())
                                    .remoteLogicSecs(agentScope.getConfig().getRemote().getLogicTtlSecs())
                                    .remotePhysicalSecs(agentScope.getConfig().getRemote().getPhysicalTtlSecs())
                                    .build())
                            .writeTier(WriteTier.ALL)
                            .build();
                    agentScope.getMultiLevelCache().putAll(putAllCmd);
                }
            }

            HitTierInfo hitTierInfo = new DefaultHitTierInfo(resultHitTierMap);
            return BaseCacheResult.batchHit(agentScope.getComponentName(), resultValueHolderMap, hitTierInfo);
        } catch (Exception e) {
            log.warn("batchGet failed. key={}, fields={}, error={}", bizKey, bizFields, e.getMessage(), e);
            return BaseCacheResult.fail(agentScope.getComponentName(), e);
        }
    }
}
