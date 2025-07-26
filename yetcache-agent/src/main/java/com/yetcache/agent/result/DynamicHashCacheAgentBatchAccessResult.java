package com.yetcache.agent.result;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;
import com.yetcache.core.result.CacheOutcome;
import lombok.Getter;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
@Getter
public final class DynamicHashCacheAgentBatchAccessResult<F, V>
        extends BaseResult<Map<F, CacheValueHolder<V>>> {
    private Map<F, HitTier> hitTierMap;
    private HitTier hitTier;

    DynamicHashCacheAgentBatchAccessResult(String cacheName,
                                           CacheOutcome outcome,
                                           Throwable e) {
        this(cacheName, outcome, null, null, e);
    }

    DynamicHashCacheAgentBatchAccessResult(String cacheName,
                                           CacheOutcome outcome,
                                           Map<F, CacheValueHolder<V>> valueHolderMap,
                                           Map<F, HitTier> hitTierMap) {
        this(cacheName, outcome, valueHolderMap, hitTierMap, null);
    }

    DynamicHashCacheAgentBatchAccessResult(String cacheName,
                                           CacheOutcome outcome,
                                           Map<F, CacheValueHolder<V>> valueHolderMap,
                                           Map<F, HitTier> hitTierMap,
                                           Throwable throwable) {
        super(cacheName, outcome, valueHolderMap, throwable);
        this.hitTierMap = hitTierMap;
    }

    public static <F, V> DynamicHashCacheAgentBatchAccessResult<F, V> hit(String cacheName,
                                                                          Map<F, CacheValueHolder<V>> valueHolderMap,
                                                                          Map<F, HitTier> hitTierMap) {
        return new DynamicHashCacheAgentBatchAccessResult<>(cacheName, CacheOutcome.HIT, valueHolderMap, hitTierMap);
    }

    public static <F, V> DynamicHashCacheAgentBatchAccessResult<F, V> fail(String cacheName,
                                                                           Throwable e) {
        return new DynamicHashCacheAgentBatchAccessResult<>(cacheName, CacheOutcome.FAIL, e);
    }

    public static <F, V> DynamicHashCacheAgentBatchAccessResult<F, V> success(String cacheName) {
        return new DynamicHashCacheAgentBatchAccessResult<>(cacheName, CacheOutcome.SUCCESS, null);
    }

}
