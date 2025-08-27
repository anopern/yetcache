package com.yetcache.agent.core.structure.kv.loader;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
public class AbstractKvCacheLoader<K, V> implements KvCacheLoader<K, V> {

    @Override
    public V load(K bizKey) {
        return null;
    }

    @Override
    public Map<K, V> batchLoad(List<K> bizKeys) {
        return Collections.emptyMap();
    }
}
