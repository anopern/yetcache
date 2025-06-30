package com.yetcache.core.cache.flathash;

import com.yetcache.core.cache.result.singlehash.FlatHashCacheGetResult;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public interface FlatHashCache<K, V> {
    V get(K bizField);

    V get(String tenantCode, K bizField);

    void refresh(K bizField);

    void invalidate(K bizField);

    Map<K, V> listAll(boolean forceRefresh);

    FlatHashCacheGetResult<K, V> getWithResult(K bizField);
}
