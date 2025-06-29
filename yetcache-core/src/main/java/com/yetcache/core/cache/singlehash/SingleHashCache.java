package com.yetcache.core.cache.singlehash;

import java.util.Collection;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public interface SingleHashCache<K, V> {
    V get(K field);

    void refresh(K field);

    void invalidate(K field);

    Map<K, V> listAll(boolean forceRefresh);
}
