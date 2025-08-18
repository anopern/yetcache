package com.yetcache.agent.core.structure.hash.get;

import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.structure.hash.HashAgentScope;
import com.yetcache.agent.core.structure.hash.HashCacheLoadCommand;
import com.yetcache.agent.interceptor.*;
import com.yetcache.core.cache.command.HashCacheGetCommand;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.codec.TypeRef;
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
    public boolean supportStructureBehaviorKey(StructureBehaviorKey sbKey) {
        return StructureType.DYNAMIC_HASH.equals(sbKey.getStructureType())
                && BehaviorType.SINGLE_GET.equals(sbKey.getBehaviorType());
    }


    @Override
    public CacheResult invoke(CacheInvocationContext ctx, ChainRunner runner) throws Throwable {
        HashCacheAgentGetInvocationCommand cmd = (HashCacheAgentGetInvocationCommand) ctx.getCommand();
        Object bizKey = cmd.getBizKey();
        Object bizField = cmd.getBizField();
        HashAgentScope agentScope = (HashAgentScope) ctx.getAgentScope();
        String componentName = agentScope.getComponentName();
        try {
            TypeRef<?> valueTypeRef = agentScope.getTypeDescriptor().getValueTypeRef();
            HashCacheGetCommand storeGetCmd = new HashCacheGetCommand(bizKey, bizField, valueTypeRef);
            CacheResult storeResult = agentScope.getMultiLevelCache().get(storeGetCmd);
            if (storeResult.code() == 0 && HitTier.NONE != storeResult.hitTierInfo().hitTier()) {
                CacheValueHolder<?> holder = (CacheValueHolder<?>) storeResult.value();
                if (holder.isNotLogicExpired()) {
                    return BaseCacheResult.singleHit(componentName, holder, storeResult.hitTierInfo());
                }
            }
            HashCacheLoadCommand loadCmd = new HashCacheLoadCommand(bizKey, bizField);
            // 回源加载数据
            CacheResult loadResult = agentScope.getCacheLoader().load(loadCmd);
            if (loadResult.isSuccess() && null == loadResult.value()) {
                return BaseCacheResult.miss(componentName);
            }
            Map<Object, Object> bizFieldValueNap = Collections.singletonMap(bizField, loadResult.value());
            // 封装为缓存值并写入缓存

            agentScope.getHashCacheFillPort().fillAndBroadcast(bizKey, bizFieldValueNap);

            return BaseCacheResult.singleHit(componentName, CacheValueHolder.wrap(loadResult.value(), 0),
                    HitTier.SOURCE);
        } catch (Exception e) {
            log.warn("cache load failed, agent = {}, key = {}, field = {}", componentName, bizKey, bizField, e);
            return BaseCacheResult.fail(componentName, e);
        }
    }
}
