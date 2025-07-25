package com.yetcache.agent.core.structure.dynamichash;

import com.yetcache.agent.result.DynamicHashCacheAgentBatchAccessResult;
import com.yetcache.agent.result.DynamicHashCacheAgentSingleAccessResult;

import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface DynamicHashCacheAgent<K, F, V> {
    DynamicHashCacheAgentSingleAccessResult<V> get(K bizKey, F bizField);

    DynamicHashCacheAgentBatchAccessResult<F, V> batchGet(K bizKey, List<F> bizFields);

    DynamicHashCacheAgentBatchAccessResult<F, V> listAll(K bizKey);

    DynamicHashCacheAgentBatchAccessResult<Void, Void> batchRefresh(K, List<F> bizKeyMap);

    DynamicHashCacheAgentBatchAccessResult<Void, Void> refreshAll(K bizKey);

    DynamicHashCacheAgentSingleAccessResult<Void> remove(K bizKey, F bizField);

    DynamicHashCacheAgentBatchAccessResult<Void, Void> removeAll(K bizKey);

    DynamicHashCacheAgentSingleAccessResult<Void> put(K bizKey, F bizField, V value);

    DynamicHashCacheAgentBatchAccessResult<Void, Void> putAll(K bizKey, Map<F, V> valueMap);
}
