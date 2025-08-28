package com.yetcache.agent.interceptor;

import com.yetcache.agent.core.StructureBehaviorKey;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/8/1
 */
public class CacheInvocationChainBuilder {
    private final CacheInvocationInterceptorRegistry interceptorRegistry;

    public CacheInvocationChainBuilder(CacheInvocationInterceptorRegistry interceptorRegistry) {
        this.interceptorRegistry = interceptorRegistry;
    }

    public CacheInvocationChain build(StructureBehaviorKey sbKey) {
        List<CacheInterceptor> interceptors = interceptorRegistry.getChainFor(sbKey);
        return new DefaultCacheInvocationChain(interceptors);
    }
}
