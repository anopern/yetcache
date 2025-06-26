package com.yetcache.core.config;

import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/26
 */
@Data
public class CacheConfig {
    protected Integer maxSize = Integer.MAX_VALUE;
    protected Long ttlSec;
}
