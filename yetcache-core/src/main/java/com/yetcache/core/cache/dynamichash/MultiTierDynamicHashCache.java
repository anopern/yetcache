package com.yetcache.core.cache.dynamichash;

import com.yetcache.core.cache.command.SingleHashCachePutCommand;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.result.BaseSingleResult;
import com.yetcache.core.result.CacheResult;
import com.yetcache.core.result.SingleCacheResultV2;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface MultiTierDynamicHashCache<K, F, V> {
    CacheResult get(K bizKey, F bizField);

//    BaseBatchResult<F, V> batchGet(K bizKey, List<F> bizFields);

//    DynamicCacheStorageBatchAccessResult<F, V> listAll(K bizKey);

    BaseSingleResult<Void> put(SingleHashCachePutCommand<K, F, V> cmd);

//    BaseBatchResult<Void, Void> putAll(K bizKey, Map<F, CacheValueHolder<V>> valueHolderMap);

//    BaseSingleResult<Void> invalidate(K bizKey, F bizField);

//    BaseBatchResult<Void, Void> invalidateAll(K bizKey);
}
