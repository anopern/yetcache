package com.yetcache.agent.governance.plugin;

import com.yetcache.agent.core.BehaviorType;
import com.yetcache.agent.core.StructureBehaviorKey;
import com.yetcache.agent.core.StructureType;
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
        return "cache-agent-metrics";
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public boolean supportStructureBehaviorKey(StructureBehaviorKey sb) {
        return sb.getStructureType() == StructureType.HASH && sb.getBehaviorType() == BehaviorType.GET;
    }

    @Override
    public CacheResult invoke(CacheInvocationContext context, ChainRunner runner) throws Throwable {
        long startNs = System.nanoTime();
        CacheResult result = runner.proceed(context);
        long costNs = System.nanoTime() - startNs;

        CacheInvocationCommand cmd = context.getCommand();
        BehaviorType behaviorType = cmd.sbKey().getBehaviorType();
        String method = behaviorType.name();
        String outcome = String.valueOf(result.code());
        if (behaviorType == BehaviorType.GET) {
            /* ---------- Counter ---------- */
            Counter.builder("yetcache.access.count")
                    .tag("cache", cmd.cacheAgentName())
                    .tag("method", method)
                    .tag("outcome", outcome)
                    .tag("hit-tier", Optional.ofNullable(result.hitLevelInfo().hitLevel())
                            .map(Enum::name).orElse(""))
                    .register(registry)
                    .increment();

            /* ---------- Timer (微秒级) ---------- */
            Timer.builder("yetcache.access.latency")
                    .publishPercentileHistogram(true)
                    .tag("cache", cmd.cacheAgentName())
                    .tag("method", method)
                    .tag("outcome", outcome)
                    .tag("hit-tier", Optional.ofNullable(result.hitLevelInfo().hitLevel())
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
