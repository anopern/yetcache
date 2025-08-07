package com.yetcache.agent.core.structure.dynamichash;


import com.yetcache.core.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
public abstract class AbstractDynamicHashCacheLoader implements DynamicHashCacheLoader {

    @Override
    public CacheResult load(HashCacheSingleLoadCommand cmd) {
        throw new UnsupportedOperationException();
    }
}
