package com.yetcache.agent;

import com.yetcache.agent.interceptor.CacheInvocationChain;
import com.yetcache.agent.interceptor.CacheInvocationContext;
import com.yetcache.agent.interceptor.CacheInvocationInterceptor;
import com.yetcache.core.result.CacheAccessResult;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.concurrent.TimeUnit;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public class MetricsInterceptor  implements CacheInvocationInterceptor {
    private final MeterRegistry registry;

    public MetricsInterceptor(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public <R extends CacheAccessResult<?>> R intercept(CacheInvocationContext ctx, CacheInvocationChain<R> chain) throws Throwable {
        long startNs = System.nanoTime();
        R res = chain.proceed(ctx);
        long costNs = System.nanoTime() - startNs;

        /* ---------- Counter ---------- */
        Counter.builder("yetcache.access.count")
                .tag("cache", ctx.getCacheName())
                .tag("method", ctx.getMethodName())              // get/listAll/refreshAll…
                .tag("outcome", res.outcome().name())      // HIT/MISS/FAIL…
                .register(registry)
                .increment();

        /* ---------- Timer (微秒级) ---------- */
        Timer.builder("yetcache.access.latency")
                .tag("cache", ctx.getCacheName())
                .tag("method", ctx.getMethodName())
                .tag("outcome", res.outcome().name())
                .register(registry)
                .record(costNs, TimeUnit.NANOSECONDS);

        return res;
    }

//    @Override
//    public  T intercept(CacheInvocationContext ctx,
//                                    CacheInvocationChain<T> chain) throws Throwable {
//

//    }
}
