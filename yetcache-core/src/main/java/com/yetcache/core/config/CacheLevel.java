package com.yetcache.core.config;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
public enum CacheLevel {
    LOCAL, REMOTE, BOTH;

    public boolean includesLocal() {
        return this == LOCAL || this == BOTH;
    }

    public boolean includesRemote() {
        return this == REMOTE || this == BOTH;
    }
}
