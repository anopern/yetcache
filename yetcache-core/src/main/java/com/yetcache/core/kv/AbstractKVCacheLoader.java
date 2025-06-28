package com.yetcache.core.kv;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
public class AbstractKVCacheLoader<K, V> implements KVCacheLoader<K, V>{

    @Override
    public V load(K bizKey) {
        return null;
    }
}
