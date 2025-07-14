package com.yetcache.core.cache.dynamichash;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface MultiTierDynamicHashCache<K, F, V> {
    DynamicHashStorageResult<K, F, V> get(K bizKey, F bizField);

    DynamicHashStorageResult<K, F, V> list(K bizKey);

    DynamicHashStorageResult<K, F, V> put(K bizKey, F bizField, V value);

    DynamicHashStorageResult<K, F, V> putAll(K bizKey, Map<F, V> valueMap);

    DynamicHashStorageResult<K, F, V> invalidate(K bizKey, F bizField);

    DynamicHashStorageResult<K, F, V> invalidateAll(K bizKey);
}
