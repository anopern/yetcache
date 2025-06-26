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
    private CacheLayer cacheLayer = CacheLayer.BOTH;
    private TenantMode tenantMode = TenantMode.NONE;
}
