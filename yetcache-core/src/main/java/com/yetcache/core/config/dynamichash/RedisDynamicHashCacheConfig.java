package com.yetcache.core.config.dynamichash;

import com.yetcache.core.config.PenetrationProtectConfig;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Data
@NoArgsConstructor
public class RedisDynamicHashCacheConfig {
    protected Long ttlSecs;
    protected Double ttlRandomPct;
    protected PenetrationProtectConfig penetrationProtect;

    public RedisDynamicHashCacheConfig(RedisDynamicHashCacheConfig other) {
        this.ttlSecs = other.ttlSecs;
        this.ttlRandomPct = other.ttlRandomPct;
        this.penetrationProtect = other.penetrationProtect;
    }
    public static RedisDynamicHashCacheConfig defaultConfig() {
        RedisDynamicHashCacheConfig config = new RedisDynamicHashCacheConfig();
        config.setTtlSecs(20 * 60L);
        config.setTtlRandomPct(0.15);
        config.setPenetrationProtect(PenetrationProtectConfig.defaultConfig());
        return config;
    }
}
