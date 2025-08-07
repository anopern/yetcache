package com.yetcache.agent.core.structure.dynamichash.get;

import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashAgentScope;
import com.yetcache.agent.core.structure.dynamichash.HashCacheSingleLoadCommand;
import com.yetcache.agent.interceptor.*;
import com.yetcache.core.cache.command.HashCacheSingleGetCommand;
import com.yetcache.core.cache.command.HashCacheSinglePutCommand;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.result.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Slf4j
public class DynamicHashCacheGetInterceptor implements CacheInterceptor {

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
    public CacheResult invoke(CacheInvocationContext ctx,
                              CacheInvocationChain chain) throws Throwable {
        DynamicHashCacheAgentGetInvocationCommand cmd = (DynamicHashCacheAgentGetInvocationCommand) ctx.getCommand();
        Object bizKey = cmd.getBizKey();
        Object bizField = cmd.getBizField();
        DynamicHashAgentScope agentScope = (DynamicHashAgentScope) ctx.getAgentScope();
        String componentName = agentScope.getComponentName();
        try {
            HashCacheSingleGetCommand storeGetCmd = new HashCacheSingleGetCommand(bizKey, bizField);
            CacheResult storeResult = agentScope.getMultiTierCache().get(storeGetCmd);
            if (storeResult.code() == 0 && HitTier.NONE != storeResult.hitTierInfo().hitTier()) {
                CacheValueHolder<?> holder = (CacheValueHolder<?>) storeResult.value();
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

            // 封装为缓存值并写入缓存
            HashCacheSinglePutCommand storePutCmd = new HashCacheSinglePutCommand(
                    bizKey, bizField, loadResult.value(),
                    agentScope.getConfig().getLocal().getLogicTtlSecs(),
                    agentScope.getConfig().getLocal().getPhysicalTtlSecs(),
                    agentScope.getConfig().getRemote().getLogicTtlSecs(),
                    agentScope.getConfig().getRemote().getPhysicalTtlSecs());
            agentScope.getMultiTierCache().put(storePutCmd);

//            try {
//                Map<F, V> loadedMap = Collections.singletonMap(bizField, loadResult);
//                ExecutableCommand command = ExecutableCommand.dynamicHash(componentName, CacheAgentMethod.PUT_ALL,
//                        bizKey, loadedMap);
//                agentScope.getBroadcastPublisher().publish(command);
//            } catch (Exception e) {
//                log.warn("broadcast failed, agent = {}, key = {}, field = {}", componentName, bizKey, bizField, e);
//            }

            return SingleCacheResult.hit(componentName, CacheValueHolder.wrap(loadResult, 0), HitTier.SOURCE);
        } catch (Exception e) {
            log.warn("cache load failed, agent = {}, key = {}, field = {}", componentName, bizKey, bizField, e);
            return SingleCacheResult.fail(componentName, e);
        }
    }
}
