//package com.yetcache.agent.core.structure.dynamichash.get;
//
//import com.yetcache.agent.broadcast.command.ExecutableCommand;
//import com.yetcache.agent.core.CacheAgentMethod;
//import com.yetcache.agent.core.StructureType;
//import com.yetcache.agent.core.structure.dynamichash.DynamicHashAgentScope;
//import com.yetcache.agent.core.structure.dynamichash.HashLoadCommand;
//import com.yetcache.agent.interceptor.*;
//import com.yetcache.core.cache.command.HashCacheSinglePutCommand;
//import com.yetcache.core.cache.support.CacheValueHolder;
//import com.yetcache.core.cache.trace.HitTier;
//import com.yetcache.core.result.*;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.Collections;
//import java.util.Map;
//
///**
// * @author walter.yan
// * @since 2025/7/30
// */
//@Slf4j
//public class DynamicHashCacheGetInterceptorV2<K, F, V> implements CacheInterceptor {
//
//    @Override
//    public String id() {
//        return "DynamicHashCacheGetInterceptor";
//    }
//
//    @Override
//    public boolean enabled() {
//        return true;
//    }
//
//    @Override
//    public int getOrder() {
//        return 1;
//    }
//
//    @Override
//    public boolean supportStructureAndBehavior(StructureBehaviorKey structureBehaviorKey) {
//        return StructureType.DYNAMIC_HASH.equals(structureBehaviorKey.getStructureType())
//                && BehaviorType.SINGLE_GET.equals(structureBehaviorKey.getBehaviorType());
//    }
//
//
//    @Override
//    public CacheResult invoke(CacheInvocationContext ctx, CacheInvocationChain chain) throws Throwable {
//        CacheInvocationCommand cmd = ctx.getCommand();
//
//        if (!(cmd instanceof DynamicHashCacheAgentGetInvocationCommand2)) {
//            ctx.interrupt("Illegal command type for DynamicHashCacheGetInterceptor");
//            return SingleCacheResult.failure("InvalidCommand", "Expect DynamicHashCacheAgentGetInvocationCommandV2");
//        }
//
//        DynamicHashCacheAgentGetInvocationCommand2 concrete = (DynamicHashCacheAgentGetInvocationCommand2) cmd;
//
//        Object bizKey = concrete.getBizKey();
//        Object bizField = concrete.getBizField();
//        DynamicHashAgentScope<K, F, V> agentScope = ctx.asDynamicHashAgentScope();
//        String componentName = agentScope.getComponentName();
//        try {
//            SingleCacheResult<V> result = agentScope.getMultiTierCache().get(bizKey, bizField);
//            if (result.outcome() == CacheOutcome.HIT) {
//                CacheValueHolder<V> holder = result.value();
//                if (holder.isNotLogicExpired()) {
//                    return BaseSingleResult.hit(componentName, holder, result.hitTier());
//                }
//            }
//
//            // 回源加载数据
//            HashLoadCommand loadCmd = new HashLoadCommand(bizKey, bizField, null);
//            CacheResult loadResult = agentScope.getCacheLoader().load(loadCmd);
//            if (!loadResult.isSuccess()) {
//                return SingleCacheResult.miss(componentName);
//            }
//
//            // 封装为缓存值并写入缓存
//            HashCacheSinglePutCommand<K, F, V> cmd = new HashCacheSinglePutCommand<>(
//                    componentName, bizKey, bizField, loadResult,
//                    agentScope.getConfig().getLocal().getLogicTtlSecs(),
//                    agentScope.getConfig().getLocal().getPhysicalTtlSecs(),
//                    agentScope.getConfig().getRemote().getLogicTtlSecs(),
//                    agentScope.getConfig().getRemote().getPhysicalTtlSecs(),
//                    null);
//            agentScope.getMultiTierCache().put(cmd);
//
//            try {
//                Map<F, V> loadedMap = Collections.singletonMap(bizField, loadResult);
//                ExecutableCommand command = ExecutableCommand.dynamicHash(componentName, CacheAgentMethod.PUT_ALL,
//                        bizKey, loadedMap);
//                agentScope.getBroadcastPublisher().publish(command);
//            } catch (Exception e) {
//                log.warn("broadcast failed, agent = {}, key = {}, field = {}", componentName, bizKey, bizField, e);
//            }
//
//            return BaseSingleResult.hit(componentName, CacheValueHolder.wrap(loadResult, 0),
//                    HitTier.SOURCE);
//        } catch (Exception e) {
//            log.warn("cache load failed, agent = {}, key = {}, field = {}", componentName, bizKey, bizField, e);
//            return BaseSingleResult.fail(componentName, e);
//        }
//    }
//}
