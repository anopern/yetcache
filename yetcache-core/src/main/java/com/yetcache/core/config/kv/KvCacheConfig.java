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
public class KvCacheConfig {
    private KvCacheSpec spec;
    protected CaffeineKVCacheConfig local;
    protected RedisKVCacheConfig remote;

    public KvCacheConfig(KvCacheConfig other) {
        this.spec = other.getSpec();
        this.local = other.getLocal();
        this.remote = other.getRemote();
    }

    public static KvCacheConfig defaultConfig() {
        return new KvCacheConfig(KvCacheSpec.defaultSpec(),
                CaffeineKVCacheConfig.defaultConfig(),
                RedisKVCacheConfig.defaultConfig());
    }
}
