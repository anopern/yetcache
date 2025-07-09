package com.yetcache.core.cache.flathash;

import com.yetcache.core.cache.support.CacheValueHolder;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/7/9
 */
@Data
public class FlatHashAccessResult<V> {
    private CacheValueHolder<V> valueHolder;
    private FlatHashAccessTrace trace;
}
