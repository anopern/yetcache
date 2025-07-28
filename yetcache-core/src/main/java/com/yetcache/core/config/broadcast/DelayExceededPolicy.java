package com.yetcache.core.config.broadcast;

/**
 * @author walter.yan
 * @since 2025/7/28
 */
public enum DelayExceededPolicy {
    DROP,
    INVALIDATE,
    FORCE_REFRESH
}
