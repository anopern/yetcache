package com.yetcache.core.result;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/8/6
 */
public class DefaultHitLevelInfo implements HitLevelInfo {
    private HitLevel hitLevel;
    private Map<Object, HitLevel> hitLevelMap;

    public DefaultHitLevelInfo(HitLevel hitLevel) {
        this.hitLevel = hitLevel;
    }

    public DefaultHitLevelInfo(Map<Object, HitLevel> hitLevelMap) {
        this.hitLevelMap = hitLevelMap;
    }

    @Override
    public HitLevel hitLevel() {
        return this.hitLevel;
    }

    @Override
    public Map<Object, HitLevel> hitLevelMap() {
        return this.hitLevelMap;
    }

    public static DefaultHitLevelInfo of(HitLevel hitLevel) {
        return new DefaultHitLevelInfo(hitLevel);
    }

    public static DefaultHitLevelInfo of(Map<Object, HitLevel> hitLevelMap) {
        return new DefaultHitLevelInfo(hitLevelMap);
    }
}
