package com.yetcache.agent.core.structure.dynamichash.get;

import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashAgentScope;
import com.yetcache.agent.core.structure.dynamichash.HashCacheSingleLoadCommand;
import com.yetcache.agent.interceptor.*;
import com.yetcache.core.cache.command.HashCacheSingleGetCommand;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.result.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Slf4j
public class HashCacheGetInterceptor implements CacheInterceptor {

    @Override
    public String id() {
        return "DynamicHashCacheGetInterceptor";
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
                && BehaviorType.SINGLE_GET.equals(structureBehaviorKey.getBehaviorType());
    }


    @Override
    public CacheResult invoke(CacheInvocationContext ctx, ChainRunner runner) throws Throwable {
        DynamicHashCacheAgentGetInvocationCommand cmd = (DynamicHashCacheAgentGetInvocationCommand) ctx.getCommand();
        Object bizKey = cmd.getBizKey();
        Object bizField = cmd.getBizField();
        DynamicHashAgentScope agentScope = (DynamicHashAgentScope) ctx.getAgentScope();
        String componentName = agentScope.getComponentName();
        try {
            HashCacheSingleGetCommand storeGetCmd = new HashCacheSingleGetCommand(bizKey, bizField);
            CacheResult storeResult = agentScope.getMultiTierCache().get(storeGetCmd);
            if (storeResult.code() == 0 && HitTier.NONE != storeResult.hitTierInfo().hitTier()) {
                CacheValueHolder holder = (CacheValueHolder) storeResult.value();
                if (holder.isNotLogicExpired()) {
                    return SingleCacheResult.hit(componentName, holder, storeResult.hitTierInfo().hitTier());
                }
            }
            HashCacheSingleLoadCommand loadCmd = new HashCacheSingleLoadCommand(bizKey, bizField);
            // 回源加载数据
            CacheResult loadResult = agentScope.getCacheLoader().load(loadCmd);
            if (loadResult.isSuccess() && null == loadResult.value()) {
                return SingleCacheResult.miss(componentName);
            }
            Map<Object, Object> bizFieldValueNap = Collections.singletonMap(bizField, loadResult.value());
            // 封装为缓存值并写入缓存

            agentScope.getHashCacheFillPort().fillAndBroadcast(bizKey, bizFieldValueNap);

            return SingleCacheResult.hit(componentName, CacheValueHolder.wrap(loadResult.value(), 0), HitTier.SOURCE);
        } catch (Exception e) {
            log.warn("cache load failed, agent = {}, key = {}, field = {}", componentName, bizKey, bizField, e);
            return SingleCacheResult.fail(componentName, e);
        }
    }
}
