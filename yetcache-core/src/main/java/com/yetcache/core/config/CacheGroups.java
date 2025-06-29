package com.yetcache.core.config;

import com.yetcache.core.config.kv.MultiTierKVCacheConfig;
import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/26
 */
@Data
public class CacheGroups {
    @NestedConfigurationProperty
    protected Map<String, MultiTierKVCacheConfig> kv = new HashMap<>();
    protected Map<String, MultiTierKVCacheConfig> singleHash = new HashMap<>();
}
