package com.yetcache.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PenetrationProtectConfig {
    protected String prefix = "__pt__";
    protected Boolean enabled;
    protected Long ttlSecs;
    protected Long maxSize;

    public PenetrationProtectConfig(PenetrationProtectConfig src) {
        if (src == null) return;
        if (src.getPrefix() != null) this.prefix = src.getPrefix();
        if (src.getEnabled() != null) this.enabled = src.getEnabled();
        if (src.getTtlSecs() != null) this.ttlSecs = src.getTtlSecs();
        if (src.getMaxSize() != null) this.maxSize = src.getMaxSize();
    }

    public static PenetrationProtectConfig defaultConfig() {
        PenetrationProtectConfig config = new PenetrationProtectConfig();
        config.setEnabled(true);
        config.setTtlSecs(120L);
        config.setMaxSize(10000L);
        return config;
    }
}
