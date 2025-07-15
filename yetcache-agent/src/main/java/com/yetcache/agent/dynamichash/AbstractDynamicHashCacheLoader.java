package com.yetcache.agent.dynamichash;

import java.util.HashMap;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
public class AbstractDynamicHashCacheLoader<K, F, V> implements DynamicHashCacheLoader<K, F, V> {
    @Override
    public V load(K bizKey, F bizField) {
        return null;
    }

    @Override
    public Map<F, V> loadAll(K bizKey) {
        return new HashMap<>();
    }
}
