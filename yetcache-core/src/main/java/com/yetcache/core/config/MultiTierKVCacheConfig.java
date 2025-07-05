package com.yetcache.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultiTierKVCacheConfig {
    private MultiTierCacheSpec spec;
    protected CaffeineCacheConfig local;
    protected RedisCacheConfig remote;
}
