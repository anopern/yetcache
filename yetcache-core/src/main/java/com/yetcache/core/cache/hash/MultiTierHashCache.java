package com.yetcache.core.cache.hash;

import com.yetcache.core.cache.command.HashCacheBatchGetCommand;
import com.yetcache.core.cache.command.HashCacheRemoveCommand;
import com.yetcache.core.cache.command.HashCacheSingleGetCommand;
import com.yetcache.core.cache.command.HashCachePutAllCommand;
import com.yetcache.core.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface MultiTierHashCache {
    <T> CacheResult get(HashCacheSingleGetCommand cmd);

    <T> CacheResult batchGet(HashCacheBatchGetCommand cmd);

    <T> CacheResult putAll(HashCachePutAllCommand cmd);

    <T> CacheResult remove(HashCacheRemoveCommand cmd);

//    BaseBatchResult<Void, Void> invalidateAll(K bizKey);
}
