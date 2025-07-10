package com.yetcache.core.cache.flathash;


import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/7
 */
public interface MultiTierFlatHashCache< F, V> {
    V get(F bizField);

    FlatHashAccessResult<V> getWithResult(F bizField);

    Map<F, V> listAll();

    FlatHashAccessResult<V> listAllWithResult();

    boolean refreshAll();

    FlatHashAccessResult<V> refreshAllWithResult();
}
