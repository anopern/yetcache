package com.yetcache.agent.enhancer;

import com.yetcache.core.cache.flathash.FlatHashAccessResult;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
public interface CacheInvocationChain<T> {
    FlatHashAccessResult<T> proceed(CacheInvocationContext ctx) throws Throwable;
}
