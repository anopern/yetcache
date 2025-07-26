package com.yetcache.core.result;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
public class BaseSingleResult<V> extends BaseResult<CacheValueHolder<V>> implements SingleHitTierAware {
    protected HitTier hitTier;

    public BaseSingleResult(String componentName, CacheOutcome outcome, CacheValueHolder<V> value, HitTier hitTier, Throwable error) {
        super(componentName, outcome, value, error);
        this.hitTier = hitTier;
    }

    public static <V> BaseSingleResult<V> hit(String componentName, CacheValueHolder<V> valueHolder, HitTier hitTier) {
        return new BaseSingleResult<>(componentName, CacheOutcome.SUCCESS, valueHolder, hitTier, null);
    }

    public static <V> BaseSingleResult<V> miss(String componentName) {
        return new BaseSingleResult<>(componentName, CacheOutcome.MISS, null, null, null);
    }

    public static <V> BaseSingleResult<V> fail(String componentName, Throwable throwable) {
        return new BaseSingleResult<>(componentName, CacheOutcome.FAIL, null, null, throwable);
    }

    public static <Void> BaseSingleResult<Void> success(String componentName) {
        return new BaseSingleResult<>(componentName, CacheOutcome.SUCCESS, null, null, null);
    }

    @Override
    public HitTier hitTier() {
        return this.hitTier;
    }
}
