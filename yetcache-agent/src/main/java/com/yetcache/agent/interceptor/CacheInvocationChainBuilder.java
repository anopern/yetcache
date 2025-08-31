package com.yetcache.agent.interceptor;

import com.yetcache.agent.agent.StructureBehaviorKey;

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

    public CacheInvocationChain build(InterceptorSupportCriteria criteria) {
        List<CacheInterceptor> interceptors = interceptorRegistry.getChainFor(criteria);
        return new DefaultCacheInvocationChain(interceptors);
    }
}
