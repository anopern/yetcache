package com.yetcache.core.support.result;

import com.yetcache.core.support.trace.dynamichash.CacheAccessGetStatus;
import com.yetcache.core.cache.support.CacheValueHolder;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
@Data
public class CacheLookupResult<V> {
    private CacheValueHolder<V> valueHolder;
    private CacheAccessGetStatus status;
    private Throwable exception;

    public CacheLookupResult() {
    }

    public CacheLookupResult(CacheValueHolder<V> valueHolder, CacheAccessGetStatus status, Throwable exception) {
        this.valueHolder = valueHolder;
        this.status = status;
        this.exception = exception;
    }


    public void physicalMiss() {
        this.status = CacheAccessGetStatus.PHYSICAL_MISS;
    }

    public boolean isPhysicalMiss() {
        return CacheAccessGetStatus.PHYSICAL_MISS == status;
    }

    public void hit() {
        this.status = CacheAccessGetStatus.HIT;
    }

    public boolean isHit() {
        return CacheAccessGetStatus.HIT == status;
    }

    public void logicalExpired() {
        this.status = CacheAccessGetStatus.LOGIC_EXPIRED;
    }

    public boolean isLogicalExpired() {
        return CacheAccessGetStatus.LOGIC_EXPIRED == status;
    }

}
