package com.yetcache.core.kv;

import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
public interface KVCacheLoader<K, V> {
    V load(K bizKey);

    Map<K, V> batchLoad(List<K> bizKeys);
}
