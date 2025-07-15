package com.yetcache.agent.protect;

import com.yetcache.agent.interceptor.CacheAccessKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
@Slf4j
public class CompositePenetrationProtector implements PenetrationProtector {
    private final CaffeinePenetrationProtector localProtector;
    private final RedisPenetrationProtector redisProtector;

    public CompositePenetrationProtector(CaffeinePenetrationProtector localProtector,
                                         RedisPenetrationProtector redisProtector) {
        this.localProtector = localProtector;
        this.redisProtector = redisProtector;
    }

    @Override
    public boolean isMarkedAsNull(CacheAccessKey key) {
        if (localProtector != null && localProtector.isMarkedAsNull(key)) {
            return true;
        }
        if (redisProtector != null) {
            try {
                if (redisProtector.isMarkedAsNull(key)) {
                    if (localProtector != null) {
                        localProtector.markAsNull(key); // 本地同步
                    }
                    return true;
                }
            } catch (Exception e) {
                log.warn("Redis penetration check failed, key = {}", key, e);
            }
        }
        return false;
    }

    @Override
    public void markAsNull(CacheAccessKey key) {
        if (localProtector != null) {
            localProtector.markAsNull(key);
        }
        if (redisProtector != null) {
            try {
                redisProtector.markAsNull(key);
            } catch (Exception e) {
                log.warn("Redis penetration mark failed, key = {}", key, e);
            }
        }
    }

    public static CompositePenetrationProtector of(
            CaffeinePenetrationProtector local, RedisPenetrationProtector remote) {
        return new CompositePenetrationProtector(local, remote);
    }
}

