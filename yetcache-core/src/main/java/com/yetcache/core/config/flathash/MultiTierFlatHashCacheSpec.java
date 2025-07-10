package com.yetcache.core.config.flathash;

import com.yetcache.core.config.CacheTier;
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
    protected CacheTier cacheTier;
    protected TenantMode tenantMode;
    protected Boolean useHashTag;

    public MultiTierFlatHashCacheSpec(CacheTier cacheTier, TenantMode tenantMode, Boolean useHashTag) {
        this.cacheTier = cacheTier;
        this.tenantMode = tenantMode;
        this.useHashTag = useHashTag;
    }

    public static MultiTierFlatHashCacheSpec defaultSpec() {
        return new MultiTierFlatHashCacheSpec(CacheTier.BOTH, TenantMode.NONE, true);
    }
}
