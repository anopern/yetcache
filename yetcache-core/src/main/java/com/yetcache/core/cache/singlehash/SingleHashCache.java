package com.yetcache.core.cache.singlehash;

import com.yetcache.core.cache.result.kv.KVCacheGetResult;
import com.yetcache.core.cache.result.singlehash.SingleHashCacheGetResult;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public interface SingleHashCache<K, V> {
    V get(K bizField);

    void refresh(K bizField);

    void invalidate(K bizField);

    Map<K, V> listAll(boolean forceRefresh);

    SingleHashCacheGetResult<K, V> getWithResult(K bizField);
}
