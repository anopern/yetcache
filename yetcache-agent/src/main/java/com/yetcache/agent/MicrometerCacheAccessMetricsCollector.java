package com.yetcache.agent;

import com.yetcache.core.cache.flathash.CacheAccessMetricsCollector;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
@Component
public class MicrometerCacheAccessMetricsCollector implements CacheAccessMetricsCollector {
    private final Map<String, Counter> hitCounterCache = new ConcurrentHashMap<>();
    private final Map<String, Counter> missCounterCache = new ConcurrentHashMap<>();
    private final Map<String, Counter> blockCounterCache = new ConcurrentHashMap<>();

    private final MeterRegistry meterRegistry;

    @Autowired
    public MicrometerCacheAccessMetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void recordHit(String cacheName, String hitTier) {
        getHitCounter(cacheName, hitTier).increment();
    }

    @Override
    public void recordMiss(String cacheName) {
        getMissCounter(cacheName).increment();
    }

    @Override
    public void recordBlocked(String cacheName) {
        getBlockCounter(cacheName).increment();
    }

    private Counter getHitCounter(String cacheName, String tier) {
        String key = cacheName + "|" + tier;
        return hitCounterCache.computeIfAbsent(key, k ->
                Counter.builder("yetcache.hit.count")
                        .tags("cache", cacheName, "tier", tier)
                        .description("Cache hit count")
                        .register(meterRegistry)
        );
    }

    private Counter getMissCounter(String cacheName) {
        return missCounterCache.computeIfAbsent(cacheName, k ->
                Counter.builder("yetcache.miss.count")
                        .tags("cache", cacheName)
                        .description("Cache miss count")
                        .register(meterRegistry)
        );
    }

    private Counter getBlockCounter(String cacheName) {
        return blockCounterCache.computeIfAbsent(cacheName, k ->
                Counter.builder("yetcache.blocked.count")
                        .tags("cache", cacheName)
                        .description("Blocked by null-penetration cache")
                        .register(meterRegistry)
        );
    }

}
