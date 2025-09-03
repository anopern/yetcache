package com.yetcache.agent.agent.kv;

import com.yetcache.agent.agent.CacheAgent;
import com.yetcache.agent.agent.CacheAgentPutOptions;
import com.yetcache.agent.agent.CacheAgentRemoveOptions;
import com.yetcache.core.result.BaseCacheResult;
import com.yetcache.core.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface KvCacheAgent extends CacheAgent {
    <K, T> BaseCacheResult<T> get(K bizKey);

    <K, T> BaseCacheResult<Void> put(K bizKey, T value);

    <K> BaseCacheResult<Void> remove(K bizKey);

  BaseCacheResult<Void> removeLocal(String key);

    <K> BaseCacheResult<Void> remove(K bizKey, CacheAgentRemoveOptions opts);
}
