package com.yetcache.core.cache.support;

import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
@Data
public final class CacheValueHolder {
    private Object value;
    private long createdTime;
    private long expireTime;
    private long lastAccessTime;

    public CacheValueHolder(Object value) {
        this.value = value;
    }

    public CacheValueHolder(Object value, long createdTime, long expireTime) {
        this.value = value;
        this.createdTime = createdTime;
        this.expireTime = expireTime;
    }

    public static CacheValueHolder wrap(Object value, long ttlSecs) {
        long now = System.currentTimeMillis();
        long expireTime = now + TimeUnit.SECONDS.toMillis(ttlSecs);
        return new CacheValueHolder(value, now, expireTime);
    }

    public boolean isLogicExpired() {
        return System.currentTimeMillis() > expireTime;
    }

    public boolean isNotLogicExpired() {
        return System.currentTimeMillis() <= expireTime;
    }
}
