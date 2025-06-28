package com.yetcache.core;

/**
 * @author walter.yan
 * @since 2025/6/26
 */
public enum TenantMode {
    REQUIRED, OPTIONAL, NONE;

    public boolean useTenant() {
        return this != NONE;
    }
}
