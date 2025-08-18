package com.yetcache.agent.core.structure.hash;


import com.yetcache.core.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
public abstract class AbstractHashCacheLoader implements HashCacheLoader {
    @Override
    public <K, F> CacheResult load(HashCacheLoadCommand<K, F> cmd) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K, F> CacheResult batchLoad(HashCacheBatchLoadCommand<K, F> cmd) {
        throw new UnsupportedOperationException();
    }
}
