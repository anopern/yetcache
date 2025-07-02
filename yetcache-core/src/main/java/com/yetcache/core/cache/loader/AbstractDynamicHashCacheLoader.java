package com.yetcache.core.cache.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/2
 */
public abstract class AbstractDynamicHashCacheLoader<K, F, V> implements DynamicHashCacheLoader<K, F, V> {
    @Override
    public V load(K bizKey, F bizField) {
        return null;
    }

    @Override
    public Map<K, Map<F, V>> batchLoad(Map<K, List<F>> bizKeyMap) {
        return new HashMap<>();
    }
}
