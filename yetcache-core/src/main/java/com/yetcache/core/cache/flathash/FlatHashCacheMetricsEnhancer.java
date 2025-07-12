package com.yetcache.core.cache.flathash;

import com.yetcache.core.config.flathash.FlatHashCacheEnhanceConfig;
import com.yetcache.core.config.flathash.HitCountMetricsConfig;
import com.yetcache.core.support.field.FieldConverter;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
public class FlatHashCacheMetricsEnhancer<F, V> implements MultiTierFlatHashCacheBehaviorEnhancer<F, V> {

    private final String cacheName;
    private final FieldConverter<F> fieldConverter;
    private final CacheMetricsCollector collector;

    public FlatHashCacheMetricsEnhancer(String cacheName,
                                        FieldConverter<F> fieldConverter,
                                        CacheMetricsCollector collector) {
        this.cacheName = cacheName;
        this.fieldConverter = fieldConverter;
        this.collector = collector;
    }

    @Override
    public MultiTierFlatHashCache<F, V> enhance(MultiTierFlatHashCache<F, V> origin,
                                                FlatHashCacheEnhanceConfig config) {
        HitCountMetricsConfig metricsConfig = config.getHitMetrics();
        if (metricsConfig == null || !Boolean.TRUE.equals(metricsConfig.getEnabled())) {
            return origin;
        }

        return new FlatHashCacheMetricsDecorator<>(cacheName, origin, metricsConfig, fieldConverter, collector);
    }
}
