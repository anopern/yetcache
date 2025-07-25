package com.yetcache.agent.core.structure;

import com.yetcache.agent.interceptor.*;
import com.yetcache.agent.result.AbstractCacheAgentResult;
import lombok.Getter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
public abstract class AbstractCacheAgent<T extends AbstractCacheAgentResult<?>> {
    @Getter
    protected final String cacheName;
    protected final List<CacheInvocationInterceptor> interceptors = new CopyOnWriteArrayList<>();

    protected AbstractCacheAgent(String cacheName) {
        this.cacheName = cacheName;
    }

    protected <R extends T> R invoke(String method, Supplier<R> business) {
        return invoke(method, business, null);
    }

    protected <R extends T> R invoke(String method, Supplier<R> business, CacheAccessKey key) {
        CacheInvocationContext ctx = CacheInvocationContext.start(cacheName, method, key);
        CacheInvocationChain<R> chain = new DefaultInvocationChain<>(interceptors, business);

        try {
            return chain.proceed(ctx);
        } catch (Throwable t) {
            // 使用 resultClass 创建一个默认失败结果，避免强转
            return defaultFail(method, t);
        }
    }

    /**
     * 子类提供默认失败返回结果（结构相关）
     */
    protected abstract <R extends T> R defaultFail(String method, Throwable t);
}
