package com.yetcache.agent.interceptor;

import com.yetcache.core.result.Result;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
public interface CacheInvocationInterceptor {

    <R extends Result<?>> R intercept(CacheInvocationContext ctx,
                                      CacheInvocationChain<R> chain) throws Throwable;

    /**
     * 增强器是否适用于当前方法名
     */
    default boolean supports(String methodName) {
        return true; // 默认全部匹配
    }
}