package com.yetcache.agent.agent.kv.loader;


import com.yetcache.core.result.BaseCacheResult;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
public abstract class AbstractKvCacheLoader<K, V> implements KvCacheLoader<K, V> {
    @Override
    public BaseCacheResult<V> load(KvCacheLoadCommand<K> cmd) {
        throw new UnsupportedOperationException();
    }
}
