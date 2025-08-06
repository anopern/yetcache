package com.yetcache.core.result;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/8/6
 */
public interface HitTierInfo {
    HitTier hitTier();

    Map<Object, HitTier> getHitTierMap();
}
