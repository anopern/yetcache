package com.yetcache.agent.interceptor;

import com.yetcache.core.result.Result;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public final class DefaultInvocationChain<R extends Result<?>>
        implements CacheInvocationChain<R> {

    private final List<CacheInvocationInterceptor> interceptors;
    private final Supplier<R> target;
    private int index = 0;

    public DefaultInvocationChain(List<CacheInvocationInterceptor> interceptors,
                                  Supplier<R> target) {
        this.interceptors = interceptors;
        this.target = target;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R proceed(CacheInvocationContext ctx) throws Throwable {
        try (ctx) {                            // ctx 实现 AutoCloseable 仅负责 MDC 等资源
            if (index < interceptors.size()) {
                return interceptors.get(index++).intercept(ctx, this);
            }
            return target.get();               // 返回 R == CacheAccessResult<?>
        }
    }
}
