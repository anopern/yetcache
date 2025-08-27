package com.yetcache.agent.core.structure.kv;

import com.yetcache.agent.core.structure.CacheAgent;
import com.yetcache.core.result.BaseCacheResult;
import com.yetcache.core.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface KvCacheAgent extends CacheAgent {
    <K, T> BaseCacheResult<T> get(K bizKey);

    <K, T> CacheResult put(K bizKey, T value);

    <K> CacheResult remove(K bizKey);
}
