package com.yetcache.core.kv;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
public interface KVCacheLoader<K, V> {
    V load(K bizKey);
}
