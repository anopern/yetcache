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
public class CaffeinePenetrationProtectCache<K> extends AbstractPenetrationProtectCache<K> {
    private final Cache<String, Boolean> cache;

    public CaffeinePenetrationProtectCache(String keyPrefix, String cacheName, long ttlSeconds, long maxSize) {
        super(keyPrefix, cacheName);
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(ttlSeconds))
                .maximumSize(maxSize)
                .build();
    }


    @Override
    public void markMiss(K k) {
        cache.put(buildKey(k), true);
    }

    @Override
    public boolean isBlocked(K k) {
        return cache.getIfPresent(buildKey(k)) != null;
    }
}
