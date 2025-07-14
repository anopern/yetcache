package com.yetcache.agent.dynamichash;

import com.yetcache.agent.result.DynamicHashCacheAgentResult;
import com.yetcache.agent.result.FlatHashCacheAgentResult;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface DynamicHashCacheAgent<K, F, V> {
    DynamicHashCacheAgentResult<K, F, V> get(K bizKey, F bizField);

    DynamicHashCacheAgentResult<K, F, V> list(K bizKey);

    DynamicHashCacheAgentResult<K, F, V> refreshAll(K bizKey);

    DynamicHashCacheAgentResult<K, F, V> invalidate(K bizKey, F bizField);

    DynamicHashCacheAgentResult<K, F, V> invalidateAll(K bizKey);
}
