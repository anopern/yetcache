package com.yetcache.agent.interceptor;


import com.yetcache.core.result.Result;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/7/29
 */
public class DefaultInvocationChain<C extends InvocationContext, T, R extends Result<T>> implements InvocationChain<C, T, R> {

    private final List<? extends InvocationInterceptor<C, T, R>> interceptors;
    private int index = 0;

    public DefaultInvocationChain(List<? extends InvocationInterceptor<C, T, R>> interceptors) {
        this.interceptors = interceptors;
    }

    @Override
    public R invoke(C ctx) throws Throwable {
        if (index >= interceptors.size()) {
            throw new IllegalStateException("Invocation chain reached end without terminal interceptor. Behavior not handled: " + ctx);
        }
        InvocationInterceptor<C, T, R> interceptor = interceptors.get(index++);
        return interceptor.invoke(ctx, this);
    }
}