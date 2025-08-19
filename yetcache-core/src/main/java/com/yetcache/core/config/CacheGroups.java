package com.yetcache.core.config;

import com.yetcache.core.config.hash.HashCacheConfig;
import com.yetcache.core.config.flathash.FlatHashCacheConfig;
import com.yetcache.core.config.kv.MultiLevelKVCacheConfig;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/26
 */
@Data
public class CacheGroups {
    protected Map<String, MultiLevelKVCacheConfig> kv = new HashMap<>();
    protected Map<String, FlatHashCacheConfig> flatHash = new HashMap<>();
    protected Map<String, HashCacheConfig> dynamicHash = new HashMap<>();
}
