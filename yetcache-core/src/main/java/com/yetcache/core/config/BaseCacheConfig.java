package com.yetcache.core.config;

import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/27
 */

@Data
public class BaseCacheConfig {
    protected Long ttlSecs;

    protected Boolean penetrationProtectEnabled;
    protected Long penetrationProtectTtlSecs;
}
