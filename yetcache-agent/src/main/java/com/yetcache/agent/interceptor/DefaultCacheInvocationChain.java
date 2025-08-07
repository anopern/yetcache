package com.yetcache.agent.interceptor;


import com.yetcache.core.result.CacheResult;
import java.util.List;

/**
 * @author walter.yan
 * @since 2025/7/29
 */
public class DefaultCacheInvocationChain implements CacheInvocationChain {

    private final List<CacheInterceptor> interceptors;
    private int index = 0;

    public DefaultCacheInvocationChain(List<CacheInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    @Override
    public CacheResult proceed(CacheInvocationContext ctx) throws Throwable{
        if (index >= interceptors.size()) {
            throw new IllegalStateException("Invocation chain reached end without terminal interceptor. Behavior not handled: " + ctx);
        }
        CacheInterceptor interceptor = interceptors.get(index++);
        return interceptor.invoke(ctx, this);
    }
}