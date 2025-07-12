package com.yetcache.core.config.flathash;

import com.yetcache.core.config.TenantMode;
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

    public MultiTierFlatHashCacheSpec(Boolean useHashTag) {
        this.useHashTag = useHashTag;
    }

    public MultiTierFlatHashCacheSpec(MultiTierFlatHashCacheSpec other) {
        if (other == null) return;
        this.cacheName = other.cacheName;
        this.keyPrefix = other.keyPrefix;
        this.useHashTag = other.useHashTag;
    }


    public static MultiTierFlatHashCacheSpec defaultSpec() {
        return new MultiTierFlatHashCacheSpec(true);
    }
}
