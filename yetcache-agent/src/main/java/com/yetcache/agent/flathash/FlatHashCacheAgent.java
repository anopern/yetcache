package com.yetcache.agent.flathash;

import com.yetcache.core.cache.flathash.FlatHashAccessResult;
import com.yetcache.core.cache.support.CacheValueHolder;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/12
 */
public interface FlatHashCacheAgent<F, V> {
    V get(F field);

    FlatHashAccessResult<CacheValueHolder<V>> getWithResult(F field);

    Map<F, V> listAll();

    FlatHashAccessResult<Map<F, CacheValueHolder<V>>> listAllWithResult();

    /**
     * 通知 Agent 当前缓存数据可能已被修改。
     * 实现中将决定是否立即执行 refreshAll。
     * 注意：不会触发字段级刷新。
     */
    void notifyDirty();
}
