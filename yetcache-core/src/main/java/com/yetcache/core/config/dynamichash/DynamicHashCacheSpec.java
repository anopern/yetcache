package com.yetcache.core.config.dynamichash;

import com.yetcache.core.config.CacheTier;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/7/5
 */
@Data
@NoArgsConstructor
public class DynamicHashCacheSpec {
    protected String cacheName;
    protected String keyPrefix;

    protected CacheTier cacheTier;
    protected Boolean useHashTag;
    protected Long refreshIntervalSecs;

    public DynamicHashCacheSpec(CacheTier cacheTier, Boolean useHashTag, Long refreshIntervalSecs) {
        this.cacheTier = cacheTier;
        this.useHashTag = useHashTag;
        this.refreshIntervalSecs = refreshIntervalSecs;
    }

    public DynamicHashCacheSpec(DynamicHashCacheSpec other) {
        if (other == null) return;
        this.cacheName = other.cacheName;
        this.keyPrefix = other.keyPrefix;
        this.cacheTier = other.cacheTier;
        this.useHashTag = other.useHashTag;
        this.refreshIntervalSecs = other.refreshIntervalSecs;
    }

    public static DynamicHashCacheSpec defaultSpec() {
        return new DynamicHashCacheSpec(CacheTier.BOTH, true, 30 * 60L);
    }
}
