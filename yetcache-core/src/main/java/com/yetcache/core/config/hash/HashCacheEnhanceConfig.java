package com.yetcache.core.config.hash;

import com.yetcache.core.config.HitCountMetricsConfig;
import com.yetcache.core.config.PenetrationProtectConfig;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
@Data
public class HashCacheEnhanceConfig {
    protected PenetrationProtectConfig localPenetrationProtect;
    protected PenetrationProtectConfig remotePenetrationProtect;
    private HitCountMetricsConfig hitMetrics;

    public HashCacheEnhanceConfig() {
    }

    public HashCacheEnhanceConfig(HashCacheEnhanceConfig other) {
        if (other == null) return;
        this.localPenetrationProtect = other.localPenetrationProtect != null
                ? new PenetrationProtectConfig(other.localPenetrationProtect)
                : null;
        this.remotePenetrationProtect = other.remotePenetrationProtect != null
                ? new PenetrationProtectConfig(other.remotePenetrationProtect)
                : null;
        this.hitMetrics = other.hitMetrics != null
                ? new HitCountMetricsConfig(other.hitMetrics)
                : null;
    }

    public static HashCacheEnhanceConfig defaultConfig() {
        HashCacheEnhanceConfig config = new HashCacheEnhanceConfig();
        config.setLocalPenetrationProtect(PenetrationProtectConfig.defaultConfig());
        config.setRemotePenetrationProtect(PenetrationProtectConfig.defaultConfig());
        config.setHitMetrics(HitCountMetricsConfig.defaultConfig());
        return config;
    }
}
