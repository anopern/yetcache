package com.yetcache.agent.interceptor;

import com.yetcache.agent.core.StructureType;
import com.yetcache.core.result.Result;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/8/1
 */
public class CacheInvocationChainBuilder<C extends CacheInvocationContext, T, R extends Result<T>> {
    private final CacheInvocationInterceptorRegistry<C, T, R> interceptorRegistry;

    public CacheInvocationChainBuilder(CacheInvocationInterceptorRegistry<C, T, R> interceptorRegistry) {
        this.interceptorRegistry = interceptorRegistry;
    }

    public CacheInvocationChain<C, T, R> build(StructureBehaviorKey sbKey) {
        List<CacheInterceptor<C, T, R>> interceptors = interceptorRegistry.getChainFor(sbKey);
        return new DefaultCacheInvocationChain<>(interceptors);
    }

    public CacheInvocationChain<C, T, R> build(StructureType structureType, BehaviorType behaviorType) {
        List<CacheInterceptor<C, T, R>> interceptors = interceptorRegistry.getChainFor(structureType, behaviorType);
        return new DefaultCacheInvocationChain<>(interceptors);
    }
}
