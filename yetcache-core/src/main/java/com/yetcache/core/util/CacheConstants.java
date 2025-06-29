package com.yetcache.core.util;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
public interface CacheConstants {

    // 缓存默认过期时间
    int DEFAULT_EXPIRE = Integer.MAX_VALUE;

    // 本地缓存数量限制
    int DEFAULT_LOCAL_LIMIT = 100;

    /**
     * 缓存TTL随机化最大百分比
     */
    double MAX_TTL_RANDOMIZE_PERCENT = 0.3;
}
