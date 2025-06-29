package com.yetcache.core.config.singlehash;

import com.yetcache.core.config.BaseCacheConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CaffeineSingleHashCacheConfig extends BaseCacheConfig {
    protected Integer maxSize;
}
