package com.yetcache.core.cache.trace;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public enum CacheAccessStatus {
    HIT,
    LOGIC_EXPIRED,
    PHYSICAL_MISS,
    BLOCKED,
}
