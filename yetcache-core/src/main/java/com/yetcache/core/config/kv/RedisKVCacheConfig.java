package com.yetcache.core.config.kv;

import com.yetcache.core.config.PenetrationProtectConfig;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Data
@NoArgsConstructor
public class RedisKVCacheConfig {
    protected Long ttlSecs;
    protected Double ttlRandomPct;
    protected PenetrationProtectConfig penetrationProtect;

    public RedisKVCacheConfig(RedisKVCacheConfig src) {
        if (src == null) return;
        if (src.ttlSecs != null) this.ttlSecs = src.ttlSecs;
        if (src.ttlRandomPct != null) this.ttlRandomPct = src.ttlRandomPct;
        if (src.penetrationProtect != null) {
            this.penetrationProtect = new PenetrationProtectConfig(src.penetrationProtect);
        }
    }
    public static RedisKVCacheConfig defaultConfig() {
        RedisKVCacheConfig config = new RedisKVCacheConfig();
        config.setTtlSecs(20 * 60L);
        config.setTtlRandomPct(0.15);
        config.setPenetrationProtect(PenetrationProtectConfig.defaultConfig());
        return config;
    }
}
