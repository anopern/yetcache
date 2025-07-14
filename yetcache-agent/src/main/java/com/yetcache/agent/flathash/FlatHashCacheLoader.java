package com.yetcache.agent.flathash;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/10
 */
public interface FlatHashCacheLoader<F, V> {
    Map<F, V> loadAll();
}
