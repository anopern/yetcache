package com.yetcache.core.cache.flathash;


import com.yetcache.core.cache.support.CacheValueHolder;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/7
 */
public interface MultiTierFlatHashCache<F, V> {
    V get(F bizField);

    FlatHashAccessResult<CacheValueHolder<V>> getWithResult(F bizField);

    Map<F, V> listAll();

    FlatHashAccessResult<Map<F, CacheValueHolder<V>>> listAllWithResult();

    void putAll(Map<F, V> dataMap);

    default FlatHashAccessResult<Void> putAllWithResult(Map<F, V> map) {
        putAll(map);
        return FlatHashAccessResult.success();
    }
}
