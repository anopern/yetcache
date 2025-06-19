package com.yetcache.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
public interface KVCache<K, V> {
    Logger logger = LoggerFactory.getLogger(KVCache.class);

    V get(K key);

    void put(K key, V value);

    void invalidate(K key);

    KVCacheGetResult<K, V> getWithResult(K key);

    KVCacheResult putWithResult(K key, V value);

    KVCacheResult invalidateWithResult(K key);
}
