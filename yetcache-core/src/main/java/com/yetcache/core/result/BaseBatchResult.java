package com.yetcache.core.result;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
public class BaseBatchResult<S, T> extends BaseResult<Map<S, CacheValueHolder<T>>> implements BatchHitTierAware {
    protected Map<S, HitTier> hitTierMap;

    public BaseBatchResult(String componentName,
                           CacheOutcome outcome,
                           Map<S, CacheValueHolder<T>> valueHolderMap,
                           Map<S, HitTier> hitTierMap,
                           Throwable error) {
        super(componentName, outcome, valueHolderMap, error);
        this.hitTierMap = hitTierMap;
    }

    public static <S, T> BaseBatchResult<S, T> hit(String componentName,
                                                   Map<S, CacheValueHolder<T>> valueHolderMap,
                                                   Map<S, HitTier> hitTierMap) {
        return new BaseBatchResult<>(componentName, CacheOutcome.HIT, valueHolderMap, hitTierMap, null);
    }

    public static <S, T> BaseBatchResult<S, T> fail(String componentName,
                                                    Throwable error) {
        return new BaseBatchResult<>(componentName, CacheOutcome.FAIL, null, null, error);
    }

    public static <S, T> BaseBatchResult<S, T> success(String componentName) {
        return new BaseBatchResult<>(componentName, CacheOutcome.SUCCESS, null, null, null);
    }

    @Override
    public Map<S, HitTier> hitTierMap() {
        return this.hitTierMap;
    }
}
