package com.yetcache.core.config;

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
    protected String cacheName;

    protected CacheTier cacheTier;
    protected TenantMode tenantMode;

    protected String keyPrefix;
    protected Boolean useHashTag;
    protected Double ttlRandomPercent;

    protected CaffeineCacheConfig local;
    protected RedisCacheConfig remote;
}
