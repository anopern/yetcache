package com.yetcache.core.protect;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public interface PenetrationProtectCache<K> {
    void markMiss(K bizKey);

    boolean isBlocked(K bizKey);
}
