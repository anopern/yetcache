package com.yetcache.core.cache.loader;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
public interface FlatHashCacheLoader<K, F, V> {
    V load(K bizKey, F bizField);

    Map<F, V> loadAll();
}
