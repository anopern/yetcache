package com.yetcache.agent.protect.cache;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public interface PenetrationProtectCache {
    void add(String absKey);

    boolean contains(String absKey);
}
