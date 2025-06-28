package com.yetcache.core.config;

import com.yetcache.core.CacheTier;
import com.yetcache.core.TenantMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultiTierCacheConfig {
    protected CacheTier cacheTier = CacheTier.BOTH;
    protected TenantMode tenantMode = TenantMode.NONE;

    protected String keyPrefix;
    protected boolean useHashTag;

    protected CaffeineCacheConfig local;
    protected RedisCacheConfig remote;
}
