package com.yetcache.agent;

import com.yetcache.core.cache.flathash.CacheAccessMetricsCollector;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
@Slf4j
public class MicrometerCacheAccessMetricsCollector implements CacheAccessMetricsCollector {

    private final MeterRegistry registry;

    // 缓存 counter 避免重复创建
    private final Map<String, Counter> counterCache = new ConcurrentHashMap<>();
    private final Map<String, Timer> timerCache = new ConcurrentHashMap<>();

    public MicrometerCacheAccessMetricsCollector(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void recordAccess(String cacheName, String method, String result) {
        String key = cacheName + "|" + method + "|" + result;
        counterCache.computeIfAbsent(key, k ->
                Counter.builder("yetcache.access.count")
                        .description("Cache access count")
                        .tag("cache", cacheName)
                        .tag("method", method)
                        .tag("result", result)
                        .register(registry)
        ).increment();
    }

    @Override
    public void recordLatency(String cacheName, String method, long nanos) {
        String key = cacheName + "|" + method;
        timerCache.computeIfAbsent(key, k ->
                Timer.builder("yetcache.access.latency")
                        .description("Cache access latency")
                        .tag("cache", cacheName)
                        .tag("method", method)
                        .publishPercentileHistogram()
                        .register(registry)
        ).record(nanos, TimeUnit.NANOSECONDS);
    }
}
