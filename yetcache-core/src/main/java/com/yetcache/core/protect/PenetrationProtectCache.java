package com.yetcache.core.protect;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public interface PenetrationProtectCache {
    void markMiss(String absKey);

    boolean isBlocked(String absKey);
}
