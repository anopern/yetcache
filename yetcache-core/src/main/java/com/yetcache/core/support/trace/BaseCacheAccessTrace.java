package com.yetcache.core.support.trace;

import com.yetcache.core.cache.result.CacheAccessStatus;
import com.yetcache.core.cache.result.SourceLoadStatus;
import com.yetcache.core.config.CacheTier;
import lombok.Data;
import org.checkerframework.checker.units.qual.K;

import java.util.HashMap;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
@Data
public class BaseCacheAccessTrace{
    protected String cacheName;
    protected CacheTier cacheTier;
    protected Exception exception;
    protected Long startMills;
    protected Long endMills;
}
