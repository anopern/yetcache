package com.yetcache.agent.result;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;
import com.yetcache.core.result.CacheOutcome;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public final class DynamicHashCacheAgentBatchAccessResult<F, V>
        extends AbstractCacheAgentResult<Map<F, CacheValueHolder<V>>> {
    private Map<F, HitTier> hitTierMap;
    private HitTier hitTier;

    DynamicHashCacheAgentBatchAccessResult(CacheOutcome outcome,
                                           Map<F, CacheValueHolder<V>> valueHolderMap,
                                           String cacheName) {
        super(cacheName, outcome, valueHolderMap);
    }

}
