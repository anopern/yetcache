package com.yetcache.agent.interceptor;

import com.yetcache.core.result.Result;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
public interface CacheInvocationChain<C extends CacheInvocationContext, T, R extends Result<T>> {
    R invoke(C ctx) throws Throwable;
}