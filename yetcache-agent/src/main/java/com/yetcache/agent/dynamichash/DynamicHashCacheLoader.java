package com.yetcache.agent.dynamichash;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
public interface DynamicHashCacheLoader<K, F, V> {
    V load(K bizKey, F bizField);

    Map<F, V> loadAll(K bizKey);
}
