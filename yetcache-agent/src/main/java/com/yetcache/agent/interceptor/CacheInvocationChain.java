package com.yetcache.agent.interceptor;


import com.yetcache.agent.result.CacheAgentResult;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
public interface CacheInvocationChain<R extends CacheAgentResult<?>> {
    R proceed(CacheInvocationContext ctx) throws Throwable;   // 返回 R 本身
}