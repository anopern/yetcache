package com.yetcache.core.config.flathash;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/7/5
 */
@Data
@NoArgsConstructor
public class MultiTierFlatHashCacheSpec {
    protected String cacheName;
    protected String keyPrefix;

    protected Boolean useHashTag;
    protected Long refreshIntervalSecs;

    public MultiTierFlatHashCacheSpec(Boolean useHashTag, Long refreshIntervalSecs) {
        this.useHashTag = useHashTag;
        this.refreshIntervalSecs = refreshIntervalSecs;
    }

    public MultiTierFlatHashCacheSpec(MultiTierFlatHashCacheSpec other) {
        if (other == null) return;
        this.cacheName = other.cacheName;
        this.keyPrefix = other.keyPrefix;
        this.useHashTag = other.useHashTag;
        this.refreshIntervalSecs = other.refreshIntervalSecs;
    }

    public static MultiTierFlatHashCacheSpec defaultSpec() {
        return new MultiTierFlatHashCacheSpec(true, 30 * 60L);
    }
}
