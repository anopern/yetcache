package com.yetcache.core.config.kv;

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
    private MultiTierKVCacheSpec spec;
    protected CaffeineKVCacheConfig local;
    protected RedisKVCacheConfig remote;

    public static MultiTierKVCacheConfig defaultConfig() {
        return new MultiTierKVCacheConfig(MultiTierKVCacheSpec.defaultSpec(),
                CaffeineKVCacheConfig.defaultConfig(),
                RedisKVCacheConfig.defaultConfig());
    }
}
