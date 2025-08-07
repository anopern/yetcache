package com.yetcache.core.cache.dynamichash;

import com.yetcache.core.cache.command.HashCacheSingleGetCommand;
import com.yetcache.core.cache.command.HashCacheSinglePutCommand;
import com.yetcache.core.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface MultiTierDynamicHashCache {
    CacheResult get(HashCacheSingleGetCommand cmd);

//    BaseBatchResult<F, V> batchGet(K bizKey, List<F> bizFields);

//    DynamicCacheStorageBatchAccessResult<F, V> listAll(K bizKey);

    CacheResult put(HashCacheSinglePutCommand cmd);

//    BaseBatchResult<Void, Void> putAll(K bizKey, Map<F, CacheValueHolder<V>> valueHolderMap);

//    BaseSingleResult<Void> invalidate(K bizKey, F bizField);

//    BaseBatchResult<Void, Void> invalidateAll(K bizKey);
}
