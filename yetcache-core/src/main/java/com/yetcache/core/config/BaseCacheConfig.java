package com.yetcache.core.config;

import com.yetcache.core.CacheLayer;
import com.yetcache.core.TenantMode;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/27
 */

@Data
public class BaseCacheConfig {
    protected CacheLayer cacheLayer = CacheLayer.BOTH;
    protected TenantMode tenantMode = TenantMode.NONE;

    protected Long localTtlSecs;
    protected Long localMaxSize;
    protected Long remoteTtlSecs;

    protected Boolean penetrationProtectEnabled = true;
    protected Long localPenetrationProtectTtlSecs;
    protected Long remotePenetrationProtectTtlSecs;
}
