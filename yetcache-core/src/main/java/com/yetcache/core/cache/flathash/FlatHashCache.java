package com.yetcache.core.cache.flathash;

import com.yetcache.core.cache.result.flathash.FlatHashCacheResult;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public interface FlatHashCache<F, V> {
    V get(F bizField);

    FlatHashCacheResult<F, V> getWithResult(F bizField);

    FlatHashCacheResult<F, V> refreshAllWithResult();

//    FlatHashCacheGetResult<F, V> batGetWithResult(Collection<F> bizFields);
}
