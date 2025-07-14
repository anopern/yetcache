package com.yetcache.core.config.flathash;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/7/5
 */
@Data
@NoArgsConstructor
public class FlatHashCacheSpec {
    protected String cacheName;
    protected String keyPrefix;

    protected Boolean useHashTag;
    protected Long refreshIntervalSecs;

    public FlatHashCacheSpec(Boolean useHashTag, Long refreshIntervalSecs) {
        this.useHashTag = useHashTag;
        this.refreshIntervalSecs = refreshIntervalSecs;
    }

    public FlatHashCacheSpec(FlatHashCacheSpec other) {
        if (other == null) return;
        this.cacheName = other.cacheName;
        this.keyPrefix = other.keyPrefix;
        this.useHashTag = other.useHashTag;
        this.refreshIntervalSecs = other.refreshIntervalSecs;
    }

    public static FlatHashCacheSpec defaultSpec() {
        return new FlatHashCacheSpec(true, 30 * 60L);
    }
}
