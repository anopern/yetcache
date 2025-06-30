package com.yetcache.core.cache.result;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public enum CacheAccessStatus {
    PENDING,             // 等待
    HIT,                // 命中，未逻辑过期
    LOGIC_EXPIRED,      // 命中但逻辑过期
    PHYSICAL_MISS,       // 未命中（物理过期）
    BLOCKED,
}
