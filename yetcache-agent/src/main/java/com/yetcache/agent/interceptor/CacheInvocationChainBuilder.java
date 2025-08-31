package com.yetcache.agent.interceptor;

import com.yetcache.agent.agent.ChainKey;

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

    public CacheInvocationChain build(ChainKey chainKey) {
        List<CacheInterceptor> interceptors = interceptorRegistry.getChainFor(chainKey);
        return new DefaultCacheInvocationChain(interceptors);
    }
}
