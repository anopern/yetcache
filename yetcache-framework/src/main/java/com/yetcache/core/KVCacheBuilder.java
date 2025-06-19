package com.yetcache.core;

/**
 * @author walter.yan
 * @since 2025/6/19
 */
public interface KVCacheBuilder {
    <K, V> KVCache<K, V> build();
}
