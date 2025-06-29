package com.yetcache.core.kv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
public interface KVCache<K, V> {
    V get(K bizKey);

    Map<K, V> batchGet(List<K> bizKeys);

    void put(K bizKey, V value);

    void invalidate(K bizKey);

    CacheGetResult<K, V> getWithResult(K bizKey);

    CachePutResult<K, V> putWithResult(K bizKey, V value);

    CacheResult<K> invalidateWithResult(K bizKey);

    CacheRefreshResult<K, V> refresh(K bizKey); // 用于热点/单 key
}
