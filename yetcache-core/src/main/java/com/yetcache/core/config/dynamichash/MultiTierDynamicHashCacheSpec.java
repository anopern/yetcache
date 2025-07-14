package com.yetcache.core.config.dynamichash;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/7/5
 */
@Data
@NoArgsConstructor
public class MultiTierDynamicHashCacheSpec {
    protected String cacheName;
    protected String keyPrefix;

    protected Boolean useHashTag;
    protected Long refreshIntervalSecs;

    public MultiTierDynamicHashCacheSpec(Boolean useHashTag, Long refreshIntervalSecs) {
        this.useHashTag = useHashTag;
        this.refreshIntervalSecs = refreshIntervalSecs;
    }

    public MultiTierDynamicHashCacheSpec(MultiTierDynamicHashCacheSpec other) {
        if (other == null) return;
        this.cacheName = other.cacheName;
        this.keyPrefix = other.keyPrefix;
        this.useHashTag = other.useHashTag;
        this.refreshIntervalSecs = other.refreshIntervalSecs;
    }

    public static MultiTierDynamicHashCacheSpec defaultSpec() {
        return new MultiTierDynamicHashCacheSpec(true, 30 * 60L);
    }
}
