package com.yetcache.core.cache.dynamichash;

import com.yetcache.core.cache.command.HashCacheBatchGetCommand;
import com.yetcache.core.cache.command.HashCacheSingleGetCommand;
import com.yetcache.core.cache.command.HashCacheSinglePutAllCommand;
import com.yetcache.core.cache.command.HashCacheSinglePutCommand;
import com.yetcache.core.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface MultiTierDynamicHashCache {
    CacheResult get(HashCacheSingleGetCommand cmd);

    CacheResult batchGet(HashCacheBatchGetCommand cmd);

//    DynamicCacheStorageBatchAccessResult<F, V> listAll(K bizKey);

    CacheResult put(HashCacheSinglePutCommand cmd);

    CacheResult putAll(HashCacheSinglePutAllCommand cmd);

//    BaseSingleResult<Void> invalidate(K bizKey, F bizField);

//    BaseBatchResult<Void, Void> invalidateAll(K bizKey);
}
