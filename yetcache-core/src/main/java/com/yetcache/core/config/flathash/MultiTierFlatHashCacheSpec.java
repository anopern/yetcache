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
    protected TenantMode tenantMode;
    protected Boolean useHashTag;

    public MultiTierFlatHashCacheSpec(TenantMode tenantMode, Boolean useHashTag) {
        this.tenantMode = tenantMode;
        this.useHashTag = useHashTag;
    }

    public MultiTierFlatHashCacheSpec(MultiTierFlatHashCacheSpec other) {
        if (other == null) return;
        this.cacheName = other.cacheName;
        this.keyPrefix = other.keyPrefix;
        this.tenantMode = other.tenantMode;
        this.useHashTag = other.useHashTag;
    }


    public static MultiTierFlatHashCacheSpec defaultSpec() {
        return new MultiTierFlatHashCacheSpec(TenantMode.NONE, true);
    }
}
