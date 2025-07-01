package com.yetcache.core.cache.result;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.support.trace.flashhash.FlatHashCacheAccessTrace;
import com.yetcache.core.support.trace.kv.KVCacheAccessTrace;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
@Data
@NoArgsConstructor
public class CacheResult<K, F, V> {
    protected CacheValueHolder<V> valueHolder;
    protected KVCacheAccessTrace<K> kvTrace;
    protected FlatHashCacheAccessTrace<F> flashHashTrace;
}
