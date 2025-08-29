package com.yetcache.core.config;

import com.yetcache.core.config.kv.KvCacheConfig;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/26
 */
@Data
public class CacheGroups {
    protected Map<String, KvCacheConfig> kv = new HashMap<>();
}
