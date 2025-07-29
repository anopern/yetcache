//package com.yetcache.agent.governance.plugin;
//
//import com.yetcache.agent.interceptor.InvocationChain;
//import com.yetcache.agent.interceptor.DefaultInvocationContext;
//import com.yetcache.agent.interceptor.InvocationInterceptor;
//import com.yetcache.core.result.Result;
//import io.micrometer.core.instrument.Counter;
//import io.micrometer.core.instrument.MeterRegistry;
//import io.micrometer.core.instrument.Timer;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * @author walter.yan
// * @since 2025/7/14
// */
//public class MetricsInterceptor implements InvocationInterceptor {
//    private final MeterRegistry registry;
//
//    public MetricsInterceptor(MeterRegistry registry) {
//        this.registry = registry;
//    }
//
//    @Override
//    public <R extends Result<?>> R intercept(DefaultInvocationContext ctx, InvocationChain<R> chain) throws Throwable {
//        long startNs = System.nanoTime();
//        R res = chain.invoke(ctx);
//        long costNs = System.nanoTime() - startNs;
//
//        /* ---------- Counter ---------- */
//        Counter.builder("yetcache.access.count")
//                .tag("cache", ctx.getComponentNane())
//                .tag("method", ctx.getMethodName())              // get/listAll/refreshAll…
//                .tag("outcome", res.outcome().name())      // HIT/MISS/FAIL…
//                .register(registry)
//                .increment();
//
//        /* ---------- Timer (微秒级) ---------- */
//        Timer.builder("yetcache.access.latency")
//                .tag("cache", ctx.getComponentNane())
//                .tag("method", ctx.getMethodName())
//                .tag("outcome", res.outcome().name())
//                .register(registry)
//                .record(costNs, TimeUnit.NANOSECONDS);
//
//        return res;
//    }
//}
