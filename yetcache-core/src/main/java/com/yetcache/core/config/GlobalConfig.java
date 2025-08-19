package com.yetcache.core.config;

import com.yetcache.core.config.dynamichash.HashCacheConfig;
import com.yetcache.core.config.flathash.FlatHashCacheConfig;
import com.yetcache.core.config.kv.MultiLevelKVCacheConfig;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/26
 */
@Data
public class GlobalConfig {
    protected MultiLevelKVCacheConfig kv = MultiLevelKVCacheConfig.defaultConfig();
    protected FlatHashCacheConfig flatHash = FlatHashCacheConfig.defaultConfig();
    protected HashCacheConfig dynamicHash = HashCacheConfig.defaultConfig();
}
