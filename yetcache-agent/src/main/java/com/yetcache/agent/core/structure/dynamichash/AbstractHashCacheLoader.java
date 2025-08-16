package com.yetcache.agent.core.structure.dynamichash;


import com.yetcache.core.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
public abstract class AbstractHashCacheLoader implements HashCacheLoader {
    @Override
    public CacheResult load(HashCacheSingleLoadCommand cmd) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CacheResult batchLoad(HashCacheBatchLoadCommand cmd) {
        throw new UnsupportedOperationException();
    }
}
