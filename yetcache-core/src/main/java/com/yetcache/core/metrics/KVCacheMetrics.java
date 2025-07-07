package com.yetcache.core.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;

/**
 * @author walter.yan
 * @since 2025/7/6
 */
public class KVCacheMetrics {
    public static void countGetHit(String cacheName, String tier) {
        // 最简单的无tag counter
        Counter counter = Metrics.counter("yetcache.kv.get.hit", "cache", cacheName, "tier", tier);
        counter.increment();
    }
}
