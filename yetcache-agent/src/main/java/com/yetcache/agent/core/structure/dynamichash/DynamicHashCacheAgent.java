package com.yetcache.agent.core.structure.dynamichash;

import com.yetcache.agent.core.structure.CacheAgent;
import com.yetcache.core.result.BaseBatchResult;
import com.yetcache.core.result.BaseSingleResult;

import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface DynamicHashCacheAgent<K, F, V> extends CacheAgent {
    BaseSingleResult<V> get(K bizKey, F bizField);

    BaseBatchResult<F, V> batchGet(K bizKey, List<F> bizFields);

//    DynamicHashCacheAgentBatchAccessResult<F, V> listAll(K bizKey);

    BaseBatchResult<Void, Void> batchRefresh(K bizKey, List<F> bizKeyMap);

//    DynamicHashCacheAgentBatchAccessResult<Void, Void> refreshAll(K bizKey);

//    DynamicHashCacheAgentSingleAccessResult<Void> remove(K bizKey, F bizField);

//    DynamicHashCacheAgentBatchAccessResult<Void, Void> removeAll(K bizKey);

//    DynamicHashCacheAgentSingleAccessResult<Void> put(K bizKey, F bizField, V value);

    BaseBatchResult<Void, Void> putAll(K bizKey, Map<F, V> valueMap);
}
