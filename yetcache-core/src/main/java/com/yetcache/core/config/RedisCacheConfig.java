package com.yetcache.core.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RedisCacheConfig extends BaseCacheConfig {

    public static RedisCacheConfig defaultConfig() {
        RedisCacheConfig config = new RedisCacheConfig();
        config.setTtlSecs(20 * 60L);
        config.setTtlRandomPct(0.15);
        config.setPenetrationProtect(PenetrationProtectConfig.defaultConfig());
        return config;
    }
}
