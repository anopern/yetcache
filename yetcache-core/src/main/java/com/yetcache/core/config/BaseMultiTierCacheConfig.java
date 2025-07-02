package com.yetcache.core.config;

import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
@Data
public class BaseMultiTierCacheConfig {
    protected String cacheName;

    protected CacheTier cacheTier;
    protected TenantMode tenantMode;

    protected Boolean useHashTag;
    protected Double ttlRandomPercent;

    protected CaffeineCacheConfig local;
    protected RedisCacheConfig remote;
}
