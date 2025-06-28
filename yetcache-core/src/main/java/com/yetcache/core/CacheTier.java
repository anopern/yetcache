package com.yetcache.core;

/**
 * @author walter.yan
 * @since 2025/6/25
 */
public enum CacheTier {
    LOCAL, REMOTE, BOTH;

    public boolean useLocal() {
        return this == LOCAL || this == BOTH;
    }

    public boolean useRemote() {
        return this == REMOTE || this == BOTH;
    }
}
