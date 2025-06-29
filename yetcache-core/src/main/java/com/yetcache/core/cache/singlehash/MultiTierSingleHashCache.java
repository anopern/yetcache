package com.yetcache.core.cache.singlehash;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
public class MultiTierSingleHashCache<K, V> implements SingleHashCache<K, V>{

    @Override
    public V get(K field) {
        return null;
    }

    @Override
    public void refresh(K field) {

    }

    @Override
    public void invalidate(K field) {

    }

    @Override
    public Map<K, V> listAll(boolean forceRefresh) {
        return null;
    }
}
