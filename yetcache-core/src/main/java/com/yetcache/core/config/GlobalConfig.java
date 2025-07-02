package com.yetcache.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author walter.yan
 * @since 2025/6/26
 */
@Data
@ConfigurationPropertiesBinding
public class GlobalConfig {

    protected CacheTier cacheTier = CacheTier.BOTH;
    protected TenantMode tenantMode = TenantMode.NONE;

    protected Boolean useHashTag = false;
    protected Double ttlRandomPercent = 0.1;

    @NestedConfigurationProperty
    protected CaffeineCacheConfig kvLocal;
    @NestedConfigurationProperty
    protected RedisCacheConfig kvRemote;

    @NestedConfigurationProperty
    protected CaffeineCacheConfig flatHashLocal;
    @NestedConfigurationProperty
    protected RedisCacheConfig flatHashRemote;

    @NestedConfigurationProperty
    protected CaffeineCacheConfig dynamicHashLocal;
    @NestedConfigurationProperty
    protected RedisCacheConfig dynamicHashRemote;
}
