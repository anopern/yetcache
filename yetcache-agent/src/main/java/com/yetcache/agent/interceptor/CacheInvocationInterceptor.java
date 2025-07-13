package com.yetcache.agent.interceptor;

import com.yetcache.core.result.CacheAccessResult;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
public interface CacheInvocationInterceptor {

    <R extends CacheAccessResult<?>> R intercept(CacheInvocationContext ctx,
                                                 CacheInvocationChain<R> chain) throws Throwable;
}