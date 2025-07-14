package com.yetcache.agent.dynamichash;

import com.yetcache.agent.result.DynamicHashCacheAgentResult;

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
