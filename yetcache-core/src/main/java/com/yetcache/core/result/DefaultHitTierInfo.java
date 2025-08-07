package com.yetcache.core.result;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/8/6
 */
public class DefaultHitTierInfo implements HitTierInfo {
    private HitTier hitTier;
    private Map<Object, HitTier> hitTierMap;

    public DefaultHitTierInfo(HitTier hitTier) {
        this.hitTier = hitTier;
    }

    public DefaultHitTierInfo(Map<Object, HitTier> hitTierMap) {
        this.hitTierMap = hitTierMap;
    }

    @Override
    public HitTier hitTier() {
        return this.hitTier;
    }

    @Override
    public Map<Object, HitTier> hitTierMap() {
        return this.hitTierMap;
    }

    public static DefaultHitTierInfo of(HitTier hitTier) {
        return new DefaultHitTierInfo(hitTier);
    }

    public static DefaultHitTierInfo of(Map<Object, HitTier> hitTierMap) {
        return new DefaultHitTierInfo(hitTierMap);
    }
}
