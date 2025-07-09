package com.yetcache.core.cache.flathash;

/**
 * @author walter.yan
 * @since 2025/7/7
 */
public interface BaseMultiTierHashCache<K, F, V> {
    V get(K bizKey, F bizField);

    FlatHashAccessResult<V> getWithResult(K bizKey, F bizField);
}
