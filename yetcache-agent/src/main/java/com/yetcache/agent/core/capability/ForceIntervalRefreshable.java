package com.yetcache.agent.core.capability;

import com.yetcache.core.result.CacheResult;

/**
 * @author walter.yan
 * @since 2025/7/14
 */
public interface ForceIntervalRefreshable {
    /**
     * 组件唯一标识
     */
    String getComponentName();

    /**
     * 刷新优先级（用于平台统一调度排序）
     */
    int getPriority();

    long getRefreshIntervalSecs();

    /**
     * 执行一次强制刷新。
     * 失败应返回结构化结果用于上报和平台判断。
     */
    CacheResult intervalRefresh();
}
