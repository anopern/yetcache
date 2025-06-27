package com.yetcache.core.config;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/26
 */
@Data
public class CacheGroups {
    protected Map<String, SpecKVCacheConfig> kv = new HashMap<>();
}
