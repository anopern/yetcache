package com.yetcache.core.config;

import com.yetcache.core.CacheTier;
import com.yetcache.core.TenantMode;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Data
public class MultiTierCacheConfig {
    protected CacheTier cacheTier = CacheTier.BOTH;
    protected TenantMode tenantMode = TenantMode.NONE;
    protected CaffeineCacheConfig local;
    protected RedisCacheConfig remote;
}
