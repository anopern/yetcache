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

    public MultiTierKVCacheConfig(MultiTierKVCacheConfig src) {
        if (src == null) return;
        if (src.spec != null) this.spec = new MultiTierKVCacheSpec(src.spec);
        if (src.local != null) this.local = new CaffeineKVCacheConfig(src.local);
        if (src.remote != null) this.remote = new RedisKVCacheConfig(src.remote);
    }

    public static MultiTierKVCacheConfig defaultConfig() {
        return new MultiTierKVCacheConfig(MultiTierKVCacheSpec.defaultSpec(),
                CaffeineKVCacheConfig.defaultConfig(),
                RedisKVCacheConfig.defaultConfig());
    }
}
