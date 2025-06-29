package com.yetcache.core.kv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
public class AbstractKVCacheLoader<K, V> implements KVCacheLoader<K, V> {

    @Override
    public V load(K bizKey) {
        return null;
    }

    @Override
    public Map<K, V> batchLoad(List<K> bizKeys) {
        return new HashMap<>();
    }
}
