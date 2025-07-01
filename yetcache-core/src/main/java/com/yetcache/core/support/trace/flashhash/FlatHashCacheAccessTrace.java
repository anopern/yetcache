package com.yetcache.core.support.trace.flashhash;

import com.yetcache.core.cache.result.CacheAccessStatus;
import com.yetcache.core.cache.result.SourceLoadStatus;
import com.yetcache.core.config.CacheTier;
import com.yetcache.core.support.trace.BaseCacheAccessTrace;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FlatHashCacheAccessTrace<F> extends BaseCacheAccessTrace {
    protected Map<F, CacheAccessStatus> localStatusMap = new HashMap<>();
    protected Map<F, CacheAccessStatus> remoteStatusMap = new HashMap<>();
    protected Map<F, SourceLoadStatus> loadStatusMap = new HashMap<>();
}
