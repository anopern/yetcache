package com.yetcache.agent.interceptor;

import com.yetcache.core.result.CacheResult;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/8/7
 */
public class ChainRunner {
    private final List<CacheInterceptor> interceptors;
    private int index = 0;

    public ChainRunner(List<CacheInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public CacheResult proceed(CacheInvocationContext ctx) throws Throwable {
        if (index >= interceptors.size()) {
            throw new IllegalStateException("Chain reached end");
        }
        return interceptors.get(index++).invoke(ctx, this);
    }
}
