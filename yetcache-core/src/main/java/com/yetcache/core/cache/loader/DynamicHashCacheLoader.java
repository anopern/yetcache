package com.yetcache.core.cache.loader;

import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
public interface DynamicHashCacheLoader<K, F, V> {
    V load(K bizKey, F bizField);

    Map<K, Map<F, V>> batchLoad(Map<K, List<F>> bizKeyMap);
}
