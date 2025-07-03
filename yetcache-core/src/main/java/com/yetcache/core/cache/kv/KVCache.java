package com.yetcache.core.cache.kv;

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

    KVCacheGetResult<V> getWithResult(K bizKey);

    KVCacheRefreshResult<K, V> refresh(K bizKey);
}
