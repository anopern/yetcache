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
}
