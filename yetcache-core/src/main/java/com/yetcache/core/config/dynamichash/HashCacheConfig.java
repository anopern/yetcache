package com.yetcache.core.config.dynamichash;

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
public class HashCacheConfig {
    private HashCacheSpec spec;
    protected CaffeineHashCacheConfig local;
    protected RedisHashCacheConfig remote;

    public HashCacheConfig(HashCacheConfig other) {
        if (other == null) return;
        this.spec = other.spec != null ? new HashCacheSpec(other.spec) : null;
        this.local = other.local != null ? new CaffeineHashCacheConfig(other.local) : null;
        this.remote = other.remote != null ? new RedisHashCacheConfig(other.remote) : null;
    }

    public static HashCacheConfig defaultConfig() {
        return new HashCacheConfig(HashCacheSpec.defaultSpec(),
                CaffeineHashCacheConfig.defaultConfig(),
                RedisHashCacheConfig.defaultConfig());
    }
}
