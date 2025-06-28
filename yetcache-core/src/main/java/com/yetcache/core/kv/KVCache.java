package com.yetcache.core.kv;

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

    CacheGetResult<K, V> getWithResult(K bizKey);

    CachePutResult<K, V> putWithResult(K bizKey, V value);

    CacheResult<K> invalidateWithResult(K bizKey);

    CacheRefreshResult<K, V> refresh(K key); // 用于热点/单 key
}
