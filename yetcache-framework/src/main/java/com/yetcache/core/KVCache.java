package com.yetcache.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
public interface KVCache<K, V> {
    Logger logger = LoggerFactory.getLogger(KVCache.class);

    V get(K key);

    void put(K key, V value);

    void remove(K key);
}
