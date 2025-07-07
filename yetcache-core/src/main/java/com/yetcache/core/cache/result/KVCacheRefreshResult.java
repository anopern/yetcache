package com.yetcache.core.cache.result;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.cache.trace.KVCacheGetTrace;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/7/1
 */
@Data
public class KVCacheRefreshResult<V> {
    protected CacheValueHolder<V> valueHolder;
    protected KVCacheGetTrace trace;
}
