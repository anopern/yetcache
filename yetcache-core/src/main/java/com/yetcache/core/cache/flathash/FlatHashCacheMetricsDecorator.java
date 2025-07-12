package com.yetcache.core.cache.flathash;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.config.flathash.HitCountMetricsConfig;
import com.yetcache.core.support.field.FieldConverter;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
public class FlatHashCacheMetricsDecorator<F, V> implements MultiTierFlatHashCache<F, V> {
    private final String cacheName;
    private final MultiTierFlatHashCache<F, V> delegate;
    private final HitCountMetricsConfig config;
    private final FieldConverter<F> fieldConverter;
    private final CacheMetricsCollector collector;

    public FlatHashCacheMetricsDecorator(String cacheName,
                                         MultiTierFlatHashCache<F, V> delegate,
                                         HitCountMetricsConfig config,
                                         FieldConverter<F> fieldConverter,
                                         CacheMetricsCollector collector) {
        this.cacheName = cacheName;
        this.delegate = delegate;
        this.config = config;
        this.fieldConverter = fieldConverter;
        this.collector = collector;
    }

    @Override
    public V get(F field) {
        FlatHashAccessResult<CacheValueHolder<V>> result = getWithResult(field);
        return result != null && result.getValue() != null ? result.getValue().getValue() : null;
    }
    @Override
    public FlatHashAccessResult<CacheValueHolder<V>> getWithResult(F field) {
        FlatHashAccessResult<CacheValueHolder<V>> result = delegate.getWithResult(field);

        if (!config.getEnabled() || collector == null || result == null) {
            return result;
        }

        FlatHashCacheAccessTrace trace = result.getTrace();
        if (trace == null) {
            collector.recordMiss(cacheName);
        } else if (trace.isBlocked()) {
            collector.recordBlocked(cacheName);
        } else if (trace.getHitTier() != null) {
            collector.recordHit(cacheName, trace.getHitTier().name());
        } else {
            collector.recordMiss(cacheName);
        }

        return result;
    }

    @Override
    public Map<F, V> listAll() {
        return delegate.listAll();
    }

    @Override
    public FlatHashAccessResult<Map<F, CacheValueHolder<V>>> listAllWithResult() {
        return delegate.listAllWithResult();
    }

    @Override
    public void putAll(Map<F, V> dataMap) {
        delegate.putAll(dataMap);
    }
}
