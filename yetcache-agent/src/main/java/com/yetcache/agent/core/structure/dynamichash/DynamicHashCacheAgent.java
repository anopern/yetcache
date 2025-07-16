package com.yetcache.agent.core.structure.dynamichash;

import com.yetcache.agent.result.DynamicHashCacheAgentResult;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface DynamicHashCacheAgent<K, F, V> {
    DynamicHashCacheAgentResult<K, F, V> get(K bizKey, F bizField);

    DynamicHashCacheAgentResult<K, F, V> listAll(K bizKey);

    DynamicHashCacheAgentResult<K, F, V> refreshAll(K bizKey);

    DynamicHashCacheAgentResult<K, F, V> remove(K bizKey, F bizField);

    DynamicHashCacheAgentResult<K, F, V> removeAll(K bizKey);
}
