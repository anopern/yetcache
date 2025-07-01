package com.yetcache.core.support.trace;

/**
 * @author walter.yan
 * @since 2025/7/2
 */
public enum CacheBatchAccessStatus {
    NOT_EXECUTED,       // 默认值，尚未调用 record
    ALL_SUCCESS,        // 全部 field 成功加载
    PARTIAL_SUCCESS,    // 部分加载成功
    ALL_FAILED,         // 无一个成功 or 提前失败
    EXCEPTION_BEFORE_LOOP, // loadAll() 直接异常
    ALL_NO_VALUE //  loadAll() 无值
    ;
}