package com.yetcache.core.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CaffeineCacheConfig extends BaseCacheConfig {
    protected Integer maxSize;

    public static CaffeineCacheConfig defaultConfig() {
        CaffeineCacheConfig defaultConfig = new CaffeineCacheConfig();
        defaultConfig.setTtlSecs(300L);
        defaultConfig.setTtlRandomPct(0.15);
        defaultConfig.setMaxSize(10000);
        defaultConfig.setPenetrationProtect(PenetrationProtectConfig.defaultConfig());
        return defaultConfig;
    }
}
