package com.yetcache.core.result;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
public class BaseSingleResult<V> implements SingleResult<CacheValueHolder<V>> {
    @Override
    public CacheOutcome outcome() {
        return null;
    }

    @Override
    public CacheValueHolder<CacheValueHolder<V>> value() {
        return null;
    }

    @Override
    public Throwable error() {
        return null;
    }

    @Override
    public HitTier hitTier() {
        return null;
    }
}
