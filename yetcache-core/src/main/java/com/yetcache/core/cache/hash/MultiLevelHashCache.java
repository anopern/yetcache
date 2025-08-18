package com.yetcache.core.cache.hash;

import com.yetcache.core.cache.command.*;
import com.yetcache.core.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface MultiLevelHashCache {
    <T> CacheResult get(HashCacheGetCommand cmd);

    <T> CacheResult batchGet(HashCacheBatchGetCommand cmd);

    <T> CacheResult putAll(HashCachePutAllCommand cmd);

    CacheResult remove(HashCacheRemoveCommand cmd);

    CacheResult batchRemove(HashCacheBatchRemoveCommand cmd);
}
