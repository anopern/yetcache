package com.yetcache.core.cache.flathash;

import com.yetcache.core.cache.result.CacheResult;
import com.yetcache.core.cache.result.singlehash.FlatHashCacheGetResult;

import java.util.Collection;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public interface FlatHashCache<K, F, V> {
    V get(F bizField);

    V get(K bizKey, F bizField);

    CacheResult<K, F, V> getWithResult(K bizKey, F bizField);

//    FlatHashCacheGetResult<F, V> batGetWithResult(Collection<F> bizFields);
}
