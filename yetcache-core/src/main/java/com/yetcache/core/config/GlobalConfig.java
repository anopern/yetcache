package com.yetcache.core.config;

import com.yetcache.core.config.kv.CaffeineKVCacheConfig;
import com.yetcache.core.config.kv.RedisKVCacheConfig;
import com.yetcache.core.config.singlehash.CaffeineFlatHashCacheConfig;
import com.yetcache.core.config.singlehash.RedisFlatHashCacheConfig;
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

    protected CaffeineKVCacheConfig kvLocal;
    protected RedisKVCacheConfig kvRemote;

    protected CaffeineFlatHashCacheConfig flatHashLocal;
    protected RedisFlatHashCacheConfig flatHashRemote;
}
