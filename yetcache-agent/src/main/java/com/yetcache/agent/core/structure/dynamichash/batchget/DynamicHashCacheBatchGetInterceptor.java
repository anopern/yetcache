package com.yetcache.agent.core.structure.dynamichash.batchget;

import cn.hutool.core.collection.CollUtil;
import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashAgentScope;
import com.yetcache.agent.core.structure.dynamichash.HashCacheBatchLoadCommand;
import com.yetcache.agent.interceptor.*;
import com.yetcache.core.cache.command.HashCacheBatchGetCommand;
import com.yetcache.core.cache.command.HashCacheSinglePutAllCommand;
import com.yetcache.core.cache.support.CacheValueHolder;
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
public class DynamicHashCacheBatchGetInterceptor implements CacheInterceptor {

    @Override
    public String id() {
        return "DynamicHashCacheBatchGetInterceptor";
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public boolean supportStructureBehaviorKey(StructureBehaviorKey structureBehaviorKey) {
        return StructureType.DYNAMIC_HASH.equals(structureBehaviorKey.getStructureType())
                && BehaviorType.BATCH_GET.equals(structureBehaviorKey.getBehaviorType());
    }


    @Override
    @SuppressWarnings("unchecked")
    public CacheResult invoke(CacheInvocationContext ctx, ChainRunner runner) throws Throwable {
        DynamicHashCacheAgentBatchGetInvocationCommand cmd =
                (DynamicHashCacheAgentBatchGetInvocationCommand) ctx.getCommand();
        Object bizKey = cmd.getBizKey();
        List<Object> bizFields = cmd.getBizFields();
        DynamicHashAgentScope agentScope = (DynamicHashAgentScope) ctx.getAgentScope();
        Map<Object, CacheValueHolder<Object>> resultValueHolderMap = new HashMap<>();
        Map<Object, HitTier> resultHitTierMap = new HashMap<>();

        try {
            // Step 1: 读缓存
            HashCacheBatchGetCommand batchGetCmd = new HashCacheBatchGetCommand(bizKey, bizFields);
            CacheResult storeResult = agentScope.getMultiTierCache().batchGet(batchGetCmd);

            List<Object> missedFields = new ArrayList<>();
            Map<Object, CacheValueHolder<Object>> cacheValueHolderMap = (Map<Object, CacheValueHolder<Object>>) storeResult.value();
            Map<Object, HitTier> cacheHitTierMap = storeResult.hitTierInfo().hitTierMap();

            for (Object field : bizFields) {
                CacheValueHolder<Object> holder = cacheValueHolderMap.get(field);
                if (holder != null && holder.isNotLogicExpired()) {
                    resultValueHolderMap.put(field, holder);
                    resultHitTierMap.put(field, cacheHitTierMap.get(field));
                } else {
                    missedFields.add(field);
                }
            }

            // Step 2: 回源
            if (!missedFields.isEmpty()) {
                HashCacheBatchLoadCommand loadCmd = new HashCacheBatchLoadCommand(bizKey, missedFields);
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
                            CacheValueHolder<Object> holder = CacheValueHolder.wrap(loaded, 0);
                            resultValueHolderMap.put(field, holder);
                            resultHitTierMap.put(field, HitTier.SOURCE);
                        }
                    }
                    // Step 3: 回写缓存
                    HashCacheSinglePutAllCommand putAllCmd = new HashCacheSinglePutAllCommand(bizKey, loadedMap,
                            agentScope.getConfig().getLocal().getLogicTtlSecs(),
                            agentScope.getConfig().getLocal().getPhysicalTtlSecs(),
                            agentScope.getConfig().getRemote().getLogicTtlSecs(),
                            agentScope.getConfig().getRemote().getPhysicalTtlSecs());
                    agentScope.getMultiTierCache().putAll(putAllCmd);
                }
            }

            HitTierInfo hitTierInfo = new DefaultHitTierInfo(resultHitTierMap);
            return BaseCacheResult.hit(agentScope.getComponentName(), resultValueHolderMap, hitTierInfo);
        } catch (Exception e) {
            log.warn("batchGet failed. key={}, fields={}, error={}", bizKey, bizFields, e.getMessage(), e);
            return BaseCacheResult.fail(agentScope.getComponentName(), e);
        }
    }
}
