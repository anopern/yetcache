package com.yetcache.core.cache.loader;

import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
public interface SingleHashCacheLoader<K, V> {
    V load(K bizField);

    Map<K, V> loadAll();
}
