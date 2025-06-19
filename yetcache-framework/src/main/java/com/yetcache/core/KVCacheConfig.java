package com.yetcache.core;

import java.time.Duration;
import java.util.function.Function;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
public class KVCacheConfig<K, V> {
    // 缓存过期时间
    private long expireAfterWriteMills = CacheConstants.DEFAULT_EXPIRE * 1000L;

    // 缓存key转换器
    private Function<K, Object> keyConvertor;

    // 缓存穿透保护，默认开启
    private boolean cachePenetrationProtect = true;

    // 缓存穿透保护时间，默认5分钟
    private Duration penetrationProtectTimeout = Duration.ofMinutes(5);

    public long getExpireAfterWriteMills() {
        return expireAfterWriteMills;
    }

    public void setExpireAfterWriteMills(long expireAfterWriteMills) {
        this.expireAfterWriteMills = expireAfterWriteMills;
    }

    public Function<K, Object> getKeyConvertor() {
        return keyConvertor;
    }

    public void setKeyConvertor(Function<K, Object> keyConvertor) {
        this.keyConvertor = keyConvertor;
    }

    public boolean isCachePenetrationProtect() {
        return cachePenetrationProtect;
    }

    public void setCachePenetrationProtect(boolean cachePenetrationProtect) {
        this.cachePenetrationProtect = cachePenetrationProtect;
    }

    public Duration getPenetrationProtectTimeout() {
        return penetrationProtectTimeout;
    }

    public void setPenetrationProtectTimeout(Duration penetrationProtectTimeout) {
        this.penetrationProtectTimeout = penetrationProtectTimeout;
    }
}
