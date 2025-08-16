package com.yetcache.agent.core.structure.hash;

import com.yetcache.agent.core.PutAllOptions;
import com.yetcache.agent.core.structure.CacheAgent;
import com.yetcache.core.result.CacheResult;
import com.yetcache.core.result.SingleCacheResult;
import java.util.List;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface HashCacheAgent extends CacheAgent {
    <K, F, T> SingleCacheResult<T> get(K bizKey, F bizField);

    <K, F, T>    CacheResult batchGet(K bizKey, List<F> bizFields);

//    DynamicHashCacheAgentBatchAccessResult<F, V> listAll(K bizKey);

//    BaseBatchResult<Void, Void> batchRefresh(K bizKey, List<F> bizKeyMap);

//    DynamicHashCacheAgentBatchAccessResult<Void, Void> refreshAll(K bizKey);

    <K, F, T> CacheResult remove(K bizKey, F bizField);

//    BaseSingleResult<Void, Void> removeAll(K bizKey);

//    BaseBatchResult<Void, Void> invalidateFields(K bizKey, List<F> bizFields);


//    DynamicHashCacheAgentSingleAccessResult<Void> put(K bizKey, F bizField, V value);

    <K, F, T> CacheResult putAll(K bizKey, Map<F, T> valueMap, PutAllOptions opts);

    <K, F, T> CacheResult putAllToLocal(String key, Map<String, T> valueMap);
}
