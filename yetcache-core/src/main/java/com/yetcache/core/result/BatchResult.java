package com.yetcache.core.result;

import com.yetcache.core.cache.trace.HitTier;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
public interface BatchResult<S, T> extends Result<T> {
    HitTier hitTier();

    Map<S, HitTier> hitTierMap();
}
