package com.yetcache.core.cache.flathash;

import com.yetcache.core.cache.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public interface FlatHashCache<K, F, V> {
    V get(F bizField);

    CacheResult<K, F, V> getWithResult(F bizField);

    CacheResult<K, F, V> refreshAllWithResult();

//    FlatHashCacheGetResult<F, V> batGetWithResult(Collection<F> bizFields);
}
