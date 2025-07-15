package com.yetcache.core.cache.dynamichash;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.result.StorageCacheAccessResult;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface MultiTierDynamicHashCache<K, F, V> {
    StorageCacheAccessResult<CacheValueHolder<V>> get(K bizKey, F bizField);

    StorageCacheAccessResult<Map<F, CacheValueHolder<V>>> listAll(K bizKey);

    StorageCacheAccessResult<Void> put(K bizKey, F bizField, V value);

    StorageCacheAccessResult<Void> putAll(K bizKey, Map<F, V> valueMap);

    StorageCacheAccessResult<Void> invalidate(K bizKey, F bizField);

    StorageCacheAccessResult<Void> invalidateAll(K bizKey);
}
