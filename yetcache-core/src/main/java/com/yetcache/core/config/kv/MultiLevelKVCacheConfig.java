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
public class MultiLevelKVCacheConfig {
    private MultiLevelKVCacheSpec spec;
    protected CaffeineKVCacheConfig local;
    protected RedisKVCacheConfig remote;

    public static MultiLevelKVCacheConfig defaultConfig() {
        return new MultiLevelKVCacheConfig(MultiLevelKVCacheSpec.defaultSpec(),
                CaffeineKVCacheConfig.defaultConfig(),
                RedisKVCacheConfig.defaultConfig());
    }
}
