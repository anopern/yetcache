package com.yetcache.core.cache.dynamichash;

import com.yetcache.core.support.trace.dynamichash.DynamicHashCacheBatchGetResult;

import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/2
 */
public interface DynamicHashCache<K, F, V> {
    V get(K bizKey, F bizField);

    DynamicHashCacheBatchGetResult<K, F, V> getWithResult(K bizKey, F bizField);

    DynamicHashCacheBatchGetResult<K, F, V> refreshWithResult(K bizKey, F bizField);

    DynamicHashCacheBatchGetResult<K, F, V> batchRefreshWithResult(Map<K, List<F>> bizKeyMap);
}
