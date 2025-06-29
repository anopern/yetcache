package com.yetcache.core.cache.result;

import com.yetcache.core.config.CacheTier;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
@Data
public class BaseCacheResult<T extends BaseCacheResult<T>> {

    /**
     * 缓存名称（cacheName），用于唯一标识当前缓存逻辑，便于日志归因与监控打点。
     */
    protected String cacheName;

    /**
     * 缓存访问命中的层级（本地、本地+远程、仅远程等），来源于 CacheTier 枚举。
     * 典型值：LOCAL, REMOTE, BOTH
     */
    protected CacheTier cacheTier;

    /**
     * 实际执行使用的 key（经过 keyConverter 转换后），用于定位 Redis 或 Caffeine 中的具体 key。
     */
    protected String key;

    /**
     * 本地缓存命中状态（逻辑命中、逻辑过期、物理未命中等），来源于 CacheAccessStatus 枚举。
     * 仅在启用本地缓存时有效。
     */
    protected CacheAccessStatus localStatus;

    /**
     * 远程缓存命中状态，来源于 CacheAccessStatus 枚举。
     * 仅在启用远程缓存时有效（如 Redis）。
     */
    protected CacheAccessStatus remoteStatus;

    /**
     * 数据加载器的执行状态（是否触发加载、是否成功加载、是否触发穿透等），
     * 来源于 SourceLoadStatus 枚举，表示最终是否走了数据源加载。
     */
    protected SourceLoadStatus loadStatus;

    /**
     * 缓存访问或加载过程中出现的异常信息（若有），可用于日志回溯与问题归因。
     * 注意：不一定表示失败，有些异常可能在 fallback 机制中被吞并。
     */
    protected Exception exception;

    /**
     * 缓存访问流程的起始时间（毫秒），用于记录缓存调用的性能指标。
     */
    protected Long startMills;

    /**
     * 缓存访问流程的结束时间（毫秒），用于记录缓存调用的耗时与观测行为。
     */
    protected Long endMills;

    @SuppressWarnings("unchecked")
    public T end() {
        this.endMills = System.currentTimeMillis();
        return (T) this;
    }

    public long durationMillis() {
        return endMills - startMills;
    }
}
