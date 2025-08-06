package com.yetcache.core.result;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/8/6
 */
public class DefaultHitTierInfo implements HitTierInfo{
    private HitTier hitTier;

    public DefaultHitTierInfo(HitTier hitTier) {
        this.hitTier = hitTier;
    }

    @Override
    public HitTier hitTier() {
        return null;
    }

    @Override
    public Map<Object, HitTier> getHitTierMap() {
        return null;
    }
}
