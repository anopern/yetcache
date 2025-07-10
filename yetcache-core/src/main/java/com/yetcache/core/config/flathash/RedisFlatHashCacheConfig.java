package com.yetcache.core.config.flathash;

import com.yetcache.core.config.PenetrationProtectConfig;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Data
@NoArgsConstructor
public class RedisFlatHashCacheConfig {
    protected Long ttlSecs;
    protected Double ttlRandomPct;
    protected PenetrationProtectConfig penetrationProtect;

    public static RedisFlatHashCacheConfig defaultConfig() {
        RedisFlatHashCacheConfig config = new RedisFlatHashCacheConfig();
        config.setTtlSecs(20 * 60L);
        config.setTtlRandomPct(0.15);
        config.setPenetrationProtect(PenetrationProtectConfig.defaultConfig());
        return config;
    }
}
