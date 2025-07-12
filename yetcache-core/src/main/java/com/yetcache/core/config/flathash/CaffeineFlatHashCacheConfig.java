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
public class CaffeineFlatHashCacheConfig {
    protected Long ttlSecs;
    protected Double ttlRandomPct;
    protected Integer maxSize;

    public static CaffeineFlatHashCacheConfig defaultConfig() {
        CaffeineFlatHashCacheConfig defaultConfig = new CaffeineFlatHashCacheConfig();
        defaultConfig.setTtlSecs(300L);
        defaultConfig.setTtlRandomPct(0.15);
        defaultConfig.setMaxSize(10000);
        return defaultConfig;
    }
}
