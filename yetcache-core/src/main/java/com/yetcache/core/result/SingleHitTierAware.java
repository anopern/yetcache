package com.yetcache.core.result;

import com.yetcache.core.cache.trace.HitTier;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
public interface SingleHitTierAware {
    HitTier hitTier();
}