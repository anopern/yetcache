package com.yetcache.core.config;

import com.yetcache.core.config.kv.MultiTierKVCacheConfig;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/26
 */
@Data
public class GlobalConfig {
    protected MultiTierKVCacheConfig kv = MultiTierKVCacheConfig.defaultConfig();
}
