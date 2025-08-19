package com.yetcache.core.result;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/8/6
 */
public interface HitLevelInfo {
    HitLevel hitLevel();

    Map<Object, HitLevel> hitLevelMap();
}
