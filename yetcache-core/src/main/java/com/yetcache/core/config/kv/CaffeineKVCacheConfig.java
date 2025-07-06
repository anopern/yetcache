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
public class CaffeineKVCacheConfig {
    protected Long ttlSecs;
    protected Double ttlRandomPct;
    protected Integer maxSize;
    protected PenetrationProtectConfig penetrationProtect;

    public CaffeineKVCacheConfig(CaffeineKVCacheConfig src) {
        if (src == null) return;
        if (src.ttlSecs != null) this.ttlSecs = src.ttlSecs;
        if (src.ttlRandomPct != null) this.ttlRandomPct = src.ttlRandomPct;
        if (src.maxSize != null) this.maxSize = src.maxSize;
        if (src.penetrationProtect != null) {
            this.penetrationProtect = new PenetrationProtectConfig(src.penetrationProtect);
        }
    }

    public static CaffeineKVCacheConfig defaultConfig() {
        CaffeineKVCacheConfig defaultConfig = new CaffeineKVCacheConfig();
        defaultConfig.setTtlSecs(300L);
        defaultConfig.setTtlRandomPct(0.15);
        defaultConfig.setMaxSize(10000);
        defaultConfig.setPenetrationProtect(PenetrationProtectConfig.defaultConfig());
        return defaultConfig;
    }
}
