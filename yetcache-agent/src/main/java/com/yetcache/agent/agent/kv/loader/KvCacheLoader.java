package com.yetcache.agent.agent.kv.loader;

import com.yetcache.core.result.BaseCacheResult;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
public interface KvCacheLoader<K, V> {

    String getLoaderName();

    BaseCacheResult<V> load(KvCacheLoadCommand<K> cmd);

}
