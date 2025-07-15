package com.yetcache.core.result;

/**
 * Unified outcome enumeration for both read & write scenarios.
 *
 * @author walter.yan
 * @since 2025/7/13
 */
public enum CacheOutcome {
    // ---------- READ ----------
    HIT,          // 命中
    MISS,         // 未命中
    BLOCK,        // 被拦截／限流
    NOT_FUND,      // 缓存不存在
    FAIL,         // 异常失败

    // ---------- WRITE ----------
    SUCCESS,      // 写入成功
    WRITE_FAIL    // 写入失败
}