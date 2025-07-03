package com.yetcache.core.support.trace.dynamichash;

import com.yetcache.core.config.CacheTier;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
@Data
public class BaseCacheAccessTrace {
    protected String cacheName;
    protected CacheTier cacheTier;
    protected Exception exception;
    protected Long startMills;
    protected Long endMills;
}
