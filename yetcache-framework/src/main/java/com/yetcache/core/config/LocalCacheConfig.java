package com.yetcache.core.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author walter.yan
 * @since 2025/6/26
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LocalCacheConfig extends CacheConfig {
    protected Integer maxSize;
}
