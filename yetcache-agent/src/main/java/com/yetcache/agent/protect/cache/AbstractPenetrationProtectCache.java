package com.yetcache.agent.protect.cache;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public abstract class AbstractPenetrationProtectCache implements PenetrationProtectCache {
    protected final String keyPrefix;
    protected final String cacheName;

    public AbstractPenetrationProtectCache(String keyPrefix, String cacheName) {
        this.keyPrefix = keyPrefix;
        this.cacheName = cacheName;
    }

    protected String buildKey(String logicalKey) {
        return String.format("%s:{%s}:%s", keyPrefix, cacheName, logicalKey);
    }

}
