package com.yetcache.core.protect;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.K;

import java.time.Duration;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Slf4j
public class CaffeinePenetrationProtectCache extends AbstractPenetrationProtectCache {
    private final Cache<String, Boolean> cache;

    public CaffeinePenetrationProtectCache(String keyPrefix, String cacheName, long ttlSecs, long maxSize) {
        super(keyPrefix, cacheName);
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(ttlSecs))
                .maximumSize(maxSize)
                .build();
    }

    @Override
    public void markMiss(String logicalKey) {
        cache.put(buildKey(logicalKey), true);
    }

    @Override
    public boolean isBlocked(String logicalKey) {
        return cache.getIfPresent(buildKey(logicalKey)) != null;
    }
}
