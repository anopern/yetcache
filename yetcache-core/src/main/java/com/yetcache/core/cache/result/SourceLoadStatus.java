package com.yetcache.core.cache.result;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public enum SourceLoadStatus {
    PENDING,
    LOADED,        // 有值
    NO_VALUE,      // null
    ERROR          // 异常
}
