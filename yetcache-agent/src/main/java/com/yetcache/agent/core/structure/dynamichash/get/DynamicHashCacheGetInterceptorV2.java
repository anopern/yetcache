package com.yetcache.agent.core.structure.dynamichash.get;

import com.yetcache.agent.broadcast.command.ExecutableCommand;
import com.yetcache.agent.core.CacheAgentMethod;
import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.structure.dynamichash.DynamicHashAgentScope;
import com.yetcache.agent.core.structure.dynamichash.HashLoadCommand;
import com.yetcache.agent.interceptor.BehaviorType;
import com.yetcache.agent.interceptor.CacheInterceptor;
import com.yetcache.agent.interceptor.CacheInvocationChain;
import com.yetcache.agent.interceptor.StructureBehaviorKey;
import com.yetcache.agent.interceptor.v2.CacheInterceptorV2;
import com.yetcache.agent.interceptor.v2.CacheInvocationChainV2;
import com.yetcache.agent.interceptor.v2.CacheInvocationCommandV2;
import com.yetcache.agent.interceptor.v2.CacheInvocationContextV2;
import com.yetcache.core.cache.command.SingleHashCachePutCommand;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;
import com.yetcache.core.result.*;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.K;

import java.util.Collections;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Slf4j
public class DynamicHashCacheGetInterceptorV2<K, F, V> implements CacheInterceptorV2 {

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
    public boolean supportStructureAndBehavior(StructureBehaviorKey structureBehaviorKey) {
        return StructureType.DYNAMIC_HASH.equals(structureBehaviorKey.getStructureType())
                && BehaviorType.SINGLE_GET.equals(structureBehaviorKey.getBehaviorType());
    }


    @Override
    public CacheResult invoke(CacheInvocationContextV2 ctx, CacheInvocationChainV2 chain) throws Throwable {
        CacheInvocationCommandV2 cmd = ctx.getCommand();

        if (!(cmd instanceof DynamicHashCacheAgentGetInvocationCommandV2)) {
            ctx.interrupt("Illegal command type for DynamicHashCacheGetInterceptor");
            return CacheResult.failure("InvalidCommand", "Expect DynamicHashCacheAgentGetInvocationCommandV2");
        }

        DynamicHashCacheAgentGetInvocationCommandV2 concrete = (DynamicHashCacheAgentGetInvocationCommandV2) cmd;

        Object bizKey = concrete.getBizKey();
        Object bizField = concrete.getBizField();
        DynamicHashAgentScope<K, F, V> agentScope = ctx.asDynamicHashAgentScope();
        String componentName = agentScope.getComponentName();
        try {
            SingleCacheResultV2<V> result = agentScope.getMultiTierCache().get(bizKey, bizField);
            if (result.outcome() == CacheOutcome.HIT) {
                CacheValueHolder<V> holder = result.value();
                if (holder.isNotLogicExpired()) {
                    return BaseSingleResult.hit(componentName, holder, result.hitTier());
                }
            }

            // 回源加载数据
            HashLoadCommand loadCmd = new HashLoadCommand(bizKey, bizField, null);
            CacheResult loadResult = agentScope.getCacheLoader().load(loadCmd);
            if (!loadResult.isSuccess()) {
                return SingleCacheResultV2.miss(componentName);
            }

            // 封装为缓存值并写入缓存
            SingleHashCachePutCommand<K, F, V> cmd = new SingleHashCachePutCommand<>(
                    componentName, bizKey, bizField, loadResult,
                    agentScope.getConfig().getLocal().getLogicTtlSecs(),
                    agentScope.getConfig().getLocal().getPhysicalTtlSecs(),
                    agentScope.getConfig().getRemote().getLogicTtlSecs(),
                    agentScope.getConfig().getRemote().getPhysicalTtlSecs(),
                    null);
            agentScope.getMultiTierCache().put(cmd);

            try {
                Map<F, V> loadedMap = Collections.singletonMap(bizField, loadResult);
                ExecutableCommand command = ExecutableCommand.dynamicHash(componentName, CacheAgentMethod.PUT_ALL,
                        bizKey, loadedMap);
                agentScope.getBroadcastPublisher().publish(command);
            } catch (Exception e) {
                log.warn("broadcast failed, agent = {}, key = {}, field = {}", componentName, bizKey, bizField, e);
            }

            return BaseSingleResult.hit(componentName, CacheValueHolder.wrap(loadResult, 0),
                    HitTier.SOURCE);
        } catch (Exception e) {
            log.warn("cache load failed, agent = {}, key = {}, field = {}", componentName, bizKey, bizField, e);
            return BaseSingleResult.fail(componentName, e);
        }
    }
}
