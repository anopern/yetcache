package com.yetcache.agent.core.structure.dynamichash;

import com.yetcache.agent.core.structure.CacheAgent;
import com.yetcache.core.result.CacheResult;
import org.checkerframework.checker.units.qual.K;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface DynamicHashCacheAgent extends CacheAgent {
    CacheResult get(Object bizKey, Object bizField);

    CacheResult batchGet(Object bizKey, List<Object> bizFields);

//    DynamicHashCacheAgentBatchAccessResult<F, V> listAll(K bizKey);

//    BaseBatchResult<Void, Void> batchRefresh(K bizKey, List<F> bizKeyMap);

//    DynamicHashCacheAgentBatchAccessResult<Void, Void> refreshAll(K bizKey);

//    BaseSingleResult<Void> remove(K bizKey, F bizField);

//    BaseSingleResult<Void, Void> removeAll(K bizKey);

//    BaseBatchResult<Void, Void> invalidateFields(K bizKey, List<F> bizFields);


//    DynamicHashCacheAgentSingleAccessResult<Void> put(K bizKey, F bizField, V value);

//    BaseBatchResult<Void, Void> putAll(K bizKey, Map<F, V> valueMap, Long version);
}
