package com.yetcache.core.result;

import lombok.Data;
import lombok.ToString;

/**
 * @author walter.yan
 * @since 2025/8/6
 */
@ToString
@Data
public class DefaultHitLevelInfo implements HitLevelInfo {
    private HitLevel hitLevel;

    public DefaultHitLevelInfo(HitLevel hitLevel) {
        this.hitLevel = hitLevel;
    }

    @Override
    public HitLevel hitLevel() {
        return this.hitLevel;
    }

    @Override
    public boolean hit() {
        return null != hitLevel
                && (hitLevel == HitLevel.LOCAL || hitLevel == HitLevel.REMOTE || hitLevel == HitLevel.SOURCE);
    }

    public static DefaultHitLevelInfo of(HitLevel hitLevel) {
        return new DefaultHitLevelInfo(hitLevel);
    }

}
