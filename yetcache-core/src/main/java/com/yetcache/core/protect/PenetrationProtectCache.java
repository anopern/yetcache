package com.yetcache.core.protect;

import org.checkerframework.checker.units.qual.K;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public interface PenetrationProtectCache {
    void markMiss(String absKey);

    boolean isBlocked(String absKey);
}
