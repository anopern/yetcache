package com.yetcache.agent.dynamichash;

import com.yetcache.agent.flathash.FlatHashCacheLoader;
import com.yetcache.agent.interceptor.CacheInvocationInterceptor;
import com.yetcache.agent.result.DynamicHashCacheAgentResult;
import com.yetcache.core.cache.flathash.MultiTierFlatHashCache;
import com.yetcache.core.config.flathash.MultiTierFlatHashCacheConfig;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public class AbstractDynamicHashCacheAgent<K,F,V> implements DynamicHashCacheAgent<K,F,V> {


    @Override
    public DynamicHashCacheAgentResult<K, F, V> get(K bizKey, F bizField) {
        return null;
    }

    @Override
    public DynamicHashCacheAgentResult<K, F, V> list(K bizKey) {
        return null;
    }

    @Override
    public DynamicHashCacheAgentResult<K, F, V> refreshAll(K bizKey) {
        return null;
    }

    @Override
    public DynamicHashCacheAgentResult<K, F, V> invalidate(K bizKey, F bizField) {
        return null;
    }

    @Override
    public DynamicHashCacheAgentResult<K, F, V> invalidateAll(K bizKey) {
        return null;
    }
}
