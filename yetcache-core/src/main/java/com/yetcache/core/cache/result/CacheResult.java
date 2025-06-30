package com.yetcache.core.cache.result;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.support.trace.CacheAccessTrace;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
@Data
public class CacheResult<K, F, V> {
    protected CacheValueHolder<V> valueHolder;
    protected CacheAccessTrace<K, F> trace;

    public CacheResult() {

    }

    public CacheResult(CacheAccessTrace<K, F> trace) {
        this.trace = trace;
    }

    public CacheResult(CacheValueHolder<V> valueHolder, CacheAccessTrace<K, F> trace) {
        this.valueHolder = valueHolder;
        this.trace = trace;
    }
}
