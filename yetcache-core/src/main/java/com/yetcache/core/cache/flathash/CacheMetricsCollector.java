package com.yetcache.core.cache.flathash;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
public interface CacheMetricsCollector {
    void recordHit(String cacheName, String hitTier);

    void recordMiss(String cacheName);

    void recordBlocked(String cacheName);
}
