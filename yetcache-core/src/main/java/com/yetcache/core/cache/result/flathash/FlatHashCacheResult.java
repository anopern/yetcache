package com.yetcache.core.cache.result.flathash;

import com.yetcache.core.cache.result.BaseCacheResultV2;
import com.yetcache.core.support.trace.flashhash.FlatHashCacheAccessTrace;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class FlatHashCacheResult<F, V> extends BaseCacheResultV2<V> {
    protected FlatHashCacheAccessTrace<F> trace;
}
