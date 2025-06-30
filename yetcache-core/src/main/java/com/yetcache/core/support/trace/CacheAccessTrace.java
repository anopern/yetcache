package com.yetcache.core.support.trace;

import com.yetcache.core.cache.result.CacheAccessStatus;
import com.yetcache.core.cache.result.SourceLoadStatus;
import com.yetcache.core.config.CacheTier;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
@Data
public class CacheAccessTrace<K, F> {
    protected String cacheName;
    protected CacheTier cacheTier;
    protected K bizKey;
    protected Map<F, CacheAccessStatus> localStatusMap = new HashMap<>();
    protected Map<F, CacheAccessStatus> remoteStatusMap = new HashMap<>();
    protected Map<F, SourceLoadStatus> loadStatusMap = new HashMap<>();
    protected Exception exception;
    protected Long startMills;
    protected Long endMills;
}
