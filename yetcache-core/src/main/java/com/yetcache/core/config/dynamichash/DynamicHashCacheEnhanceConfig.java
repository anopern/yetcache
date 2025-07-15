package com.yetcache.core.config.dynamichash;

import com.yetcache.core.config.HitCountMetricsConfig;
import com.yetcache.core.config.PenetrationProtectConfig;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
@Data
public class DynamicHashCacheEnhanceConfig {
    protected PenetrationProtectConfig localPenetrationProtect;
    protected PenetrationProtectConfig remotePenetrationProtect;
    private HitCountMetricsConfig hitMetrics;

    public DynamicHashCacheEnhanceConfig() {
    }

    public DynamicHashCacheEnhanceConfig(DynamicHashCacheEnhanceConfig other) {
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

    public static DynamicHashCacheEnhanceConfig defaultConfig() {
        DynamicHashCacheEnhanceConfig config = new DynamicHashCacheEnhanceConfig();
        config.setLocalPenetrationProtect(PenetrationProtectConfig.defaultConfig());
        config.setRemotePenetrationProtect(PenetrationProtectConfig.defaultConfig());
        config.setHitMetrics(HitCountMetricsConfig.defaultConfig());
        return config;
    }
}
