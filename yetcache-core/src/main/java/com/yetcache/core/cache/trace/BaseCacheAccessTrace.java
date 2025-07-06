package com.yetcache.core.cache.trace;

import com.yetcache.core.config.CacheTier;
import com.yetcache.core.context.CacheAccessSources;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/7/5
 */
@Data
public class BaseCacheAccessTrace {
    protected String cacheName;
    protected CacheTier cacheTier;
    protected CacheAccessSources source;
    protected Exception exception;
    protected Long startMills;
    protected Long endMills;
}
