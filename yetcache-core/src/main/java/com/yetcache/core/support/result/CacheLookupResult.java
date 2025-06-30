package com.yetcache.core.support.result;

import com.yetcache.core.cache.result.CacheAccessStatus;
import com.yetcache.core.cache.result.SourceLoadStatus;
import com.yetcache.core.cache.support.CacheValueHolder;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
@Data
public class CacheLookupResult<V> {
    private CacheValueHolder<V> valueHolder;
    private CacheAccessStatus status;
    private Throwable exception;

    public CacheLookupResult() {
    }

    public CacheLookupResult(CacheValueHolder<V> valueHolder, CacheAccessStatus status, Throwable exception) {
        this.valueHolder = valueHolder;
        this.status = status;
        this.exception = exception;
    }


    public void physicalMiss() {
        this.status = CacheAccessStatus.PHYSICAL_MISS;
    }

    public boolean isPhysicalMiss() {
        return CacheAccessStatus.PHYSICAL_MISS == status;
    }

    public void hit() {
        this.status = CacheAccessStatus.HIT;
    }

    public boolean isHit() {
        return CacheAccessStatus.HIT == status;
    }

    public void logicalExpired() {
        this.status = CacheAccessStatus.LOGIC_EXPIRED;
    }

    public boolean isLogicalExpired() {
        return CacheAccessStatus.LOGIC_EXPIRED == status;
    }

}
