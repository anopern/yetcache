package com.yetcache.core.config;

import com.yetcache.core.config.kv.KvCacheConfig;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/26
 */
@Data
public class GlobalConfig {
    protected KvCacheConfig kv = KvCacheConfig.defaultConfig();
}
