package com.yetcache.core.support;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
@NoArgsConstructor
public final class CacheValueHolder<T> {
    @Getter
    @Setter
    private T value;
    @Getter
    @Setter
    private long createdTime;
    @Getter
    @Setter
    private long expireTime;
    @Getter
    @Setter
    private long lastAccessTime;

    public CacheValueHolder(T value) {
        this.value = value;
    }

    public CacheValueHolder(T value, long createdTime, long expireTime) {
        this.value = value;
        this.createdTime = createdTime;
        this.expireTime = expireTime;
    }

    public static <T> CacheValueHolder<T> wrap(T value, long ttlSecs) {
        long now = System.currentTimeMillis();
        long expireTime = now + TimeUnit.SECONDS.toMillis(ttlSecs);
        return new CacheValueHolder<>(value, now, expireTime);
    }

    public boolean isLogicExpired() {
        return System.currentTimeMillis() > expireTime;
    }

    public boolean isNotLogicExpired() {
        return System.currentTimeMillis() <= expireTime;
    }
}
