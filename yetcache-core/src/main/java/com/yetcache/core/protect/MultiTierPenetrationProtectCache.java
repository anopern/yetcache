package com.yetcache.core.protect;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
public class MultiTierPenetrationProtectCache {
    private final List<PenetrationProtectCache> delegates;

    public MultiTierPenetrationProtectCache(List<PenetrationProtectCache> delegates) {
        this.delegates = delegates;
    }
}
