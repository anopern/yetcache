package com.yetcache.core.cache.dynamichash;

import com.yetcache.core.result.DynamicCacheStorageBatchAccessResult;
import com.yetcache.core.result.DynamicCacheStorageSingleAccessResult;

import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface MultiTierDynamicHashCache<K, F, V> {
    DynamicCacheStorageSingleAccessResult<V> get(K bizKey, F bizField);

    DynamicCacheStorageBatchAccessResult<F, V> batchGet(K bizKey, List<F> bizFields);

    DynamicCacheStorageBatchAccessResult<F, V> listAll(K bizKey);

    DynamicCacheStorageSingleAccessResult<Void> put(K bizKey, F bizField, V value);

    DynamicCacheStorageBatchAccessResult<Void, Void> putAll(K bizKey, Map<F, V> valueMap);

    DynamicCacheStorageSingleAccessResult<Void> invalidate(K bizKey, F bizField);

    DynamicCacheStorageBatchAccessResult<Void, Void> invalidateAll(K bizKey);
}
