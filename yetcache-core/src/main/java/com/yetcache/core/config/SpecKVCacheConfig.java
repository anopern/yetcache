package com.yetcache.core.config;

import com.yetcache.core.CacheLayer;
import com.yetcache.core.TenantMode;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/26
 */
@Data
public class SpecKVCacheConfig {
    protected CacheLayer cacheLayer = CacheLayer.BOTH;
    protected TenantMode tenantMode = TenantMode.NONE;

    protected Long localTtlSec;
    protected Long localMaxSize;
    protected Long remoteTtlSec;

    protected Boolean penetrationProtectEnabled = true;
    protected Long localPenetrationProtectTtlSec;
    protected Long remotePenetrationProtectTtlSec;
}
