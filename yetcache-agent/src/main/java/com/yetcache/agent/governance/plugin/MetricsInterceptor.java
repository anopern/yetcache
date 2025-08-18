package com.yetcache.agent.governance.plugin;

import com.yetcache.agent.interceptor.*;
import com.yetcache.core.result.CacheResult;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
@Slf4j
public class MetricsInterceptor implements CacheInterceptor {
    private final MeterRegistry registry;

    public MetricsInterceptor(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String id() {
        return "hash-metrics";
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean supportStructureBehaviorKey(StructureBehaviorKey structureBehaviorKey) {
        return false;
    }

    @Override
    public CacheResult invoke(CacheInvocationContext context, ChainRunner runner) throws Throwable {
        long startNs = System.nanoTime();
        CacheResult result = runner.proceed(context);
        long costNs = System.nanoTime() - startNs;

        CacheInvocationCommand cmd = context.getCommand();
        BehaviorType behaviorType = cmd.structureBehaviorKey().getBehaviorType();
        String method = behaviorType.name();
        String outcome = String.valueOf(result.code());
        if (behaviorType == BehaviorType.GET) {
            /* ---------- Counter ---------- */
            Counter.builder("yetcache.access.count")
                    .tag("cache", cmd.componentName())
                    .tag("method", method)
                    .tag("outcome", outcome)
                    .tag("hit-tier", Optional.ofNullable(result.hitTierInfo().hitTier())
                            .map(Enum::name).orElse(""))
                    .register(registry)
                    .increment();

            /* ---------- Timer (微秒级) ---------- */
            Timer.builder("yetcache.access.latency")
                    .tag("cache", cmd.componentName())
                    .tag("method", method)
                    .tag("outcome", outcome)
                    .tag("hit-tier", Optional.ofNullable(result.hitTierInfo().hitTier())
                            .map(Enum::name).orElse(""))
                    .register(registry)
                    .record(costNs, TimeUnit.NANOSECONDS);
        } else if (behaviorType == BehaviorType.BATCH_GET) {

        } else {
            log.error("Unsupported behavior type: {}", behaviorType);
        }

        return result;
    }
}
