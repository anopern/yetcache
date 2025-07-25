package com.yetcache.agent.interceptor;


import com.yetcache.agent.result.AbstractCacheAgentResult;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
public interface CacheInvocationChain<R extends AbstractCacheAgentResult<?>> {
    R proceed(CacheInvocationContext ctx) throws Throwable;   // 返回 R 本身
}