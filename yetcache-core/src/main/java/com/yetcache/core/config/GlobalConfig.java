package com.yetcache.core.config;

import com.yetcache.core.config.dynamichash.DynamicHashCacheConfig;
import com.yetcache.core.config.flathash.FlatHashCacheConfig;
import com.yetcache.core.config.kv.MultiTierKVCacheConfig;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/26
 */
@Data
public class GlobalConfig {
    protected MultiTierKVCacheConfig kv = MultiTierKVCacheConfig.defaultConfig();
    protected FlatHashCacheConfig flatHash = FlatHashCacheConfig.defaultConfig();
    protected DynamicHashCacheConfig dynamicHash = DynamicHashCacheConfig.defaultConfig();
}
