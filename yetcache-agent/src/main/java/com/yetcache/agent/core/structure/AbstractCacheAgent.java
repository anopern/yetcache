package com.yetcache.agent.core.structure;

import com.yetcache.agent.interceptor.CacheInvocationChain;
import com.yetcache.agent.interceptor.CacheInvocationContext;
import com.yetcache.agent.interceptor.CacheInvocationInterceptor;
import com.yetcache.agent.interceptor.DefaultInvocationChain;
import com.yetcache.core.result.CacheAccessResult;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
public abstract class AbstractCacheAgent<T extends CacheAccessResult<?>> {
    @Getter
    protected final String componentName;
    protected final List<CacheInvocationInterceptor> interceptors = new CopyOnWriteArrayList<>();

    protected AbstractCacheAgent(String componentName) {
        this.componentName = componentName;
    }

    @SuppressWarnings("unchecked")
    protected <R extends T> R invoke(String method, Supplier<R> business) {
        CacheInvocationContext ctx = CacheInvocationContext.start(componentName, method);
        CacheInvocationChain<R> chain = new DefaultInvocationChain<>(interceptors, business);
        try {
            return chain.proceed(ctx);
        } catch (Throwable t) {
            return (R) defaultFail(method, t);
        }
    }

    /**
     * 子类提供默认失败返回结果（结构相关）
     */
    protected abstract T defaultFail(String method, Throwable t);
}
