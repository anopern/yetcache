package com.yetcache.core.config;

import com.yetcache.core.config.kv.CaffeineKVCacheConfig;
import com.yetcache.core.config.kv.RedisKVCacheConfig;
import com.yetcache.core.config.singlehash.CaffeineFlatHashCacheConfig;
import com.yetcache.core.config.singlehash.RedisFlatHashCacheConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
    protected CaffeineKVCacheConfig kvLocal;
    @NestedConfigurationProperty
    protected RedisKVCacheConfig kvRemote;

    @NestedConfigurationProperty
    protected CaffeineFlatHashCacheConfig flatHashLocal;
    @NestedConfigurationProperty
    protected RedisFlatHashCacheConfig flatHashRemote;
}
