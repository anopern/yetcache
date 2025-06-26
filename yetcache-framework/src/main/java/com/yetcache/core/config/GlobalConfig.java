package com.yetcache.core.config;

import com.yetcache.core.CacheLayer;
import com.yetcache.core.TenantMode;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/26
 */
@Data
public class GlobalConfig {
    protected CacheLayer cacheLayer = CacheLayer.BOTH;
    protected TenantMode tenantMode = TenantMode.NONE;

    protected Long localTtlSec = 60L;
    protected Long remoteTtlSec = 60L;

    protected Boolean allowNullValue = false;
    protected Long nullTtlSec = 60L;
    protected Boolean penetrationProtectEnable = true;
}
