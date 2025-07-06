package com.yetcache.core.config.kv;

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
public class MultiTierKVCacheSpec {
    protected String cacheName;
    protected String keyPrefix;
    protected CacheTier cacheTier;
    protected TenantMode tenantMode;
    protected Boolean useHashTag;

    public MultiTierKVCacheSpec(MultiTierKVCacheSpec src) {
        if (src == null) return;
        if (src.cacheName != null) this.cacheName = src.cacheName;
        if (src.keyPrefix != null) this.keyPrefix = src.keyPrefix;
        if (src.cacheTier != null) this.cacheTier = src.cacheTier;
        if (src.tenantMode != null) this.tenantMode = src.tenantMode;
        if (src.useHashTag != null) this.useHashTag = src.useHashTag;
    }

    public MultiTierKVCacheSpec(CacheTier cacheTier, TenantMode tenantMode, Boolean useHashTag) {
        this.cacheTier = cacheTier;
        this.tenantMode = tenantMode;
        this.useHashTag = useHashTag;
    }

    public static MultiTierKVCacheSpec defaultSpec() {
        return new MultiTierKVCacheSpec(CacheTier.BOTH, TenantMode.NONE, true);
    }
}
