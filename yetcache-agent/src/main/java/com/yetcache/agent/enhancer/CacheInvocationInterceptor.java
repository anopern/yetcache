package com.yetcache.agent.enhancer;

import com.yetcache.core.cache.flathash.FlatHashAccessResult;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
public interface CacheInvocationInterceptor {

    <T> FlatHashAccessResult<T> intercept(CacheInvocationContext ctx,
                                          CacheInvocationChain<T> chain) throws Throwable;
}