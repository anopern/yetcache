package com.yetcache.core.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@EqualsAndHashCode(callSuper = true)
@Data

public class CaffeineCacheConfig extends BaseCacheConfig {
    protected Integer maxSize;
}
