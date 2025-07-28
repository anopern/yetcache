package com.yetcache.core.cache.dynamichash;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.result.BaseBatchResult;
import com.yetcache.core.result.BaseSingleResult;

import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface MultiTierDynamicHashCache<K, F, V> {
    BaseSingleResult<V> get(K bizKey, F bizField);

    BaseBatchResult<F, V> batchGet(K bizKey, List<F> bizFields);

//    DynamicCacheStorageBatchAccessResult<F, V> listAll(K bizKey);

    BaseSingleResult<Void> put(K bizKey, F bizField, CacheValueHolder<V> valueHolder);

    BaseBatchResult<Void, Void> putAll(K bizKey, Map<F, CacheValueHolder<V>> valueHolderMap);

    BaseSingleResult<Void> invalidate(K bizKey, F bizField);

//    BaseBatchResult<Void, Void> invalidateAll(K bizKey);
}
