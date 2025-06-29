package com.yetcache.core.cache.kv;

import com.yetcache.core.cache.result.kv.BaseKVCacheResult;
import com.yetcache.core.cache.result.kv.KVCacheGetResult;
import com.yetcache.core.cache.result.kv.KVCachePutResult;
import com.yetcache.core.cache.result.kv.KVCacheRefreshResult;

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

    KVCacheGetResult<K, V> getWithResult(K bizKey);

    KVCachePutResult<K, V> putWithResult(K bizKey, V value);

    BaseKVCacheResult<K> invalidateWithResult(K bizKey);

    KVCacheRefreshResult<K, V> refresh(K bizKey); // 用于热点/单 key
}
