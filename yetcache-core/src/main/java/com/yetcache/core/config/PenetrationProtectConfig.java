package com.yetcache.core.config;

import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Data

public class PenetrationProtectConfig {
    protected String prefix = "__pt__";
    protected Boolean enabled;
    protected Long ttlSecs;
    protected Long maxSize;

    public static PenetrationProtectConfig defaultConfig() {
        PenetrationProtectConfig config = new PenetrationProtectConfig();
        config.setEnabled(true);
        config.setTtlSecs(120L);
        config.setMaxSize(10000L);
        return config;
    }
}
