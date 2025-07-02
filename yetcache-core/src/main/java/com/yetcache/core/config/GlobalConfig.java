package com.yetcache.core.config;

import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/26
 */
@Data
public class GlobalConfig {

    protected CacheTier cacheTier = CacheTier.BOTH;
    protected TenantMode tenantMode = TenantMode.NONE;

    protected Boolean useHashTag = false;
    protected Double ttlRandomPercent = 0.1;

    protected CaffeineCacheConfig kvLocal;
    protected RedisCacheConfig kvRemote;

    protected CaffeineCacheConfig flatHashLocal;
    protected RedisCacheConfig flatHashRemote;

    protected CaffeineCacheConfig dynamicHashLocal;
    protected RedisCacheConfig dynamicHashRemote;
}
