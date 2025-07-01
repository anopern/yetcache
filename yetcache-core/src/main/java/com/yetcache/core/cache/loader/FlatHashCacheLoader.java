package com.yetcache.core.cache.loader;

import org.checkerframework.checker.units.qual.K;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
public interface FlatHashCacheLoader<F, V> {
    Map<F, V> loadAll();
}
