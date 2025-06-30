package com.yetcache.core.context;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
public enum CacheAccessSources {
    /**
     * 默认来源，一般用于业务正常调用缓存
     */
    NORMAL,

    /**
     * 预加载，如定时批量 preload 热点数据
     */
    PRELOAD,

    /**
     * 间隔刷新，如 interval refresh 调度任务
     */
    REFRESH,

    /**
     * 运维接口手动触发（如 Admin 后台批量刷新、清理等）
     */
    OPS,

    /**
     * 消息驱动，如 MQ 消息触发的缓存刷新
     */
    MQ,

    /**
     * 强制回源，例如调用方指定 forceRefresh = true，跳过缓存
     */
    FORCE_REFRESH,

    /**
     * 缓存穿透保护命中，如布隆过滤器或穿透缓存直接返回 null
     */
    PENETRATION_PROTECT,

    /**
     * 预警回补场景下的缓存调用，例如因缓存延迟、异常触发的补偿加载
     */
    ALERT_BACK_FILL,

    /**
     * 测试或 mock 场景下模拟调用缓存
     */
    TEST
}