package com.yetcache.agent.interceptor;

import com.yetcache.core.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
public interface CacheInvocationChain {
    CacheResult proceed(CacheInvocationContext ctx) throws Throwable;
}