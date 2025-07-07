package com.yetcache.core.cache.kv;

import com.yetcache.core.cache.result.KVCacheGetResult;
import com.yetcache.core.cache.result.KVCacheRefreshResult;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
public interface KVCache<K, V> {
    V get(K bizKey);

    void refresh(K bizKey);

    KVCacheGetResult<V> getWithResult(K bizKey);

    KVCacheRefreshResult<V> refreshWithResult(K bizKey);
}
