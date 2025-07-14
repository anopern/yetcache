package com.yetcache.core.cache.dynamichash;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public class DefaultMultiTierDynamicHashCache<K, F, V> implements MultiTierDynamicHashCache<K, F, V> {
    @Override
    public DynamicHashStorageResult<K, F, V> get(K bizKey, F bizField) {
        return null;
    }

    @Override
    public DynamicHashStorageResult<K, F, V> list(K bizKey) {
        return null;
    }

    @Override
    public DynamicHashStorageResult<K, F, V> put(K bizKey, F bizField, V value) {
        return null;
    }

    @Override
    public DynamicHashStorageResult<K, F, V> putAll(K bizKey, Map<F, V> valueMap) {
        return null;
    }

    @Override
    public DynamicHashStorageResult<K, F, V> invalidate(K bizKey, F bizField) {
        return null;
    }

    @Override
    public DynamicHashStorageResult<K, F, V> invalidateAll(K bizKey) {
        return null;
    }
}
