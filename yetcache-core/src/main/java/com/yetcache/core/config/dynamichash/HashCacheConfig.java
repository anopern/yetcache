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
    private DynamicHashCacheSpec spec;
    protected CaffeineDynamicHashCacheConfig local;
    protected RedisDynamicHashCacheConfig remote;

    public HashCacheConfig(HashCacheConfig other) {
        if (other == null) return;
        this.spec = other.spec != null ? new DynamicHashCacheSpec(other.spec) : null;
        this.local = other.local != null ? new CaffeineDynamicHashCacheConfig(other.local) : null;
        this.remote = other.remote != null ? new RedisDynamicHashCacheConfig(other.remote) : null;
    }

    public static HashCacheConfig defaultConfig() {
        return new HashCacheConfig(DynamicHashCacheSpec.defaultSpec(),
                CaffeineDynamicHashCacheConfig.defaultConfig(),
                RedisDynamicHashCacheConfig.defaultConfig());
    }
}
