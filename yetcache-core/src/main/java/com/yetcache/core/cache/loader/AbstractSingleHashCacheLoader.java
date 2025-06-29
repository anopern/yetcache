package com.yetcache.core.cache.loader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
public abstract class AbstractSingleHashCacheLoader<K, V> implements SingleHashCacheLoader<K, V> {
    @Override
    public V load(K bizField) {
        return null;
    }

    @Override
    public Map<K, V> loadAll() {
        return new HashMap<>();
    }
}
