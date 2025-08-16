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
public class HashCacheSpec {
    protected String cacheName;
    protected String keyPrefix;

    protected CacheTier cacheTier;
    protected Boolean useHashTag;
    protected Boolean allowNullValue;
    protected Long fullyLoadedExpireSecs;
    protected Long refreshIntervalSecs;

    public HashCacheSpec(CacheTier cacheTier,
                         Boolean useHashTag,
                         Boolean allowNullValue,
                         Long fullyLoadedExpireSecs,
                         Long refreshIntervalSecs) {
        this.cacheTier = cacheTier;
        this.useHashTag = useHashTag;
        this.allowNullValue = allowNullValue;
        this.fullyLoadedExpireSecs = fullyLoadedExpireSecs;
        this.refreshIntervalSecs = refreshIntervalSecs;
    }

    public HashCacheSpec(HashCacheSpec other) {
        if (other == null) return;
        this.cacheName = other.cacheName;
        this.keyPrefix = other.keyPrefix;
        this.cacheTier = other.cacheTier;
        this.useHashTag = other.useHashTag;
        this.allowNullValue = other.allowNullValue;
        this.fullyLoadedExpireSecs = other.fullyLoadedExpireSecs;
        this.refreshIntervalSecs = other.refreshIntervalSecs;
    }

    public static HashCacheSpec defaultSpec() {
        return new HashCacheSpec(CacheTier.BOTH, true, false, 2 * 60L, 30 * 60L);
    }
}
