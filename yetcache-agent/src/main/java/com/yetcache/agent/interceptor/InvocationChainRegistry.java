package com.yetcache.agent.interceptor;

import com.yetcache.agent.core.StructureType;
import com.yetcache.core.result.Result;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author walter.yan
 * @since 2025/7/29
 */
public class InvocationChainRegistry {
    private final Map<StructureBehaviorKey, InvocationChain<? extends InvocationContext, ?, ? extends Result<?>>> chainMap = new HashMap<>();
    private final List<CacheInterceptor<? extends InvocationContext, ?, ? extends Result<?>>> interceptors = new ArrayList<>();

    public InvocationChainRegistry(List<CacheInterceptor<? extends InvocationContext, ?, ? extends Result<?>>> interceptors) {
        this.interceptors.addAll(interceptors);
    }

    public void register(StructureBehaviorKey key, InvocationChain<?, ?, ?> chain) {
        chainMap.put(key, chain);
    }

    @SuppressWarnings("unchecked")
    public <C extends InvocationContext, T, R extends Result<T>> InvocationChain<C, T, R> getChain(StructureType structureType, BehaviorType behaviorType) {
        StructureBehaviorKey key = new StructureBehaviorKey(structureType, behaviorType);
        return (InvocationChain<C, T, R>) chainMap.computeIfAbsent(key, k -> {
            List<CacheInterceptor<C, T, R>> applicable = interceptors.stream()
                    .filter(CacheInterceptor::enabled)
                    .filter(i -> i.supportStructure(k.getStructureType()))
                    .filter(i -> i.supportBehavior(k.getBehaviorType()))
                    .map(i -> (CacheInterceptor<C, T, R>) i)
                    .sorted(Comparator.comparing(CacheInterceptor::getOrder))
                    .collect(Collectors.toList());
            return new DefaultInvocationChain<>(applicable);
        });
    }

    public <C extends InvocationContext, T, R extends Result<T>> R invoke(C context) throws Throwable {
        // 1. 获取结构+行为对应的行为链
        InvocationChain<C, T, R> chain = getChain(context.structureType(), context.behaviorType());
        // 2. 驱动链执行，传入上下文
        return chain.invoke(context);
    }
}
