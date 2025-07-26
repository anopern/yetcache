package com.yetcache.agent.core.structure;

import com.yetcache.agent.interceptor.*;
import com.yetcache.core.result.Result;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
public abstract class AbstractCacheAgent {
    @Getter
    protected final String cacheName;
    protected final List<CacheInvocationInterceptor> interceptors = new CopyOnWriteArrayList<>();

    protected AbstractCacheAgent(String cacheName) {
        this.cacheName = cacheName;
    }

    protected <R extends Result<?>> R invoke(String method, Supplier<R> business) {
        return invoke(method, business, null);
    }

    protected <R extends Result<?>> R invoke(String method, Supplier<R> business, CacheAccessKey key) {
        CacheInvocationContext ctx = CacheInvocationContext.start(getCacheName(), method, key);
        CacheInvocationChain<R> chain = new DefaultInvocationChain<>(interceptors, business);
        try {
            return chain.proceed(ctx);
        } catch (Throwable t) {
            return defaultFail(method, t);
        }
    }

    /**
     * 子类提供默认失败返回结果（结构相关）
     */
    protected abstract <R extends Result<?>> R defaultFail(String method, Throwable t);
}
