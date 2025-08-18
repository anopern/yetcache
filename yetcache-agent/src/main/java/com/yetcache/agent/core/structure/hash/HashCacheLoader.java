package com.yetcache.agent.core.structure.hash;

import com.yetcache.core.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
public interface HashCacheLoader {
    CacheResult load(HashCacheLoadCommand cmd);
    CacheResult batchLoad(HashCacheBatchLoadCommand cmd);
}
