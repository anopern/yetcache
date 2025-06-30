package com.yetcache.core.cache.loader;


import java.util.HashMap;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
public abstract class AbstractFlatHashCacheLoader<K,F, V> implements FlatHashCacheLoader<K,F, V> {
    @Override
    public V load(K bizKey, F bizField) {
        return null;
    }

    @Override
    public Map<F, V> loadAll(K bizKey) {
        return new HashMap<>();
    }
}
