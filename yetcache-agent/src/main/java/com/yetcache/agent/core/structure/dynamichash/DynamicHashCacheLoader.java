package com.yetcache.agent.core.structure.dynamichash;

import com.yetcache.core.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
public interface DynamicHashCacheLoader {
    CacheResult load(HashCacheSingleLoadCommand cmd);
}
