package com.yetcache.core.cache.loader;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
public interface FlatHashCacheLoader<K, V> {
    V load(K bizField);

    Map<K, V> loadAll();
}
