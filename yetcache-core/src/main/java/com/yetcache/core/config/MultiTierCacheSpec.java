package com.yetcache.core.config;

import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/7/5
 */
@Data
public class MultiTierCacheSpec {
    protected String cacheName;
    protected String keyPrefix;
    protected CacheTier cacheTier;
    protected TenantMode tenantMode;
    protected Boolean useHashTag;
}
