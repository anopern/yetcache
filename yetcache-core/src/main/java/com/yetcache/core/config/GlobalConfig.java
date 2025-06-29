package com.yetcache.core.config;

import com.yetcache.core.CacheTier;
import com.yetcache.core.TenantMode;
import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

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

    @NestedConfigurationProperty
    protected CaffeineCacheConfig local;
    @NestedConfigurationProperty
    protected RedisCacheConfig remote;
}
