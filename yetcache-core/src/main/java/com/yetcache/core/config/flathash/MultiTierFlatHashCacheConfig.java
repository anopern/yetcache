package com.yetcache.core.config.flathash;

import com.yetcache.core.config.kv.CaffeineKVCacheConfig;
import com.yetcache.core.config.kv.MultiTierKVCacheSpec;
import com.yetcache.core.config.kv.RedisKVCacheConfig;
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
public class MultiTierFlatHashCacheConfig {
    private MultiTierFlatHashCacheSpec spec;
    protected CaffeineFlatHashCacheConfig local;
    protected RedisFlatHashCacheConfig remote;

    public static MultiTierFlatHashCacheConfig defaultConfig() {
        return new MultiTierFlatHashCacheConfig(MultiTierFlatHashCacheSpec.defaultSpec(),
                CaffeineFlatHashCacheConfig.defaultConfig(),
                RedisFlatHashCacheConfig.defaultConfig());
    }
}
