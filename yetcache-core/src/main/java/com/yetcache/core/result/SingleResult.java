package com.yetcache.core.result;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.HitTier;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
public class SingleResult<V> extends BaseResult<CacheValueHolder<V>> implements SingleHitTierAware {
    protected HitTier hitTier;

    @Override
    public HitTier hitTier() {
        return this.hitTier;
    }
}
