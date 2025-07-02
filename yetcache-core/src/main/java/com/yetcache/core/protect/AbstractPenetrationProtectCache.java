package com.yetcache.core.protect;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
public abstract class AbstractPenetrationProtectCache<K> implements PenetrationProtectCache<K> {
    protected final String keyPrefix;
    protected final String cacheName;

    public AbstractPenetrationProtectCache(String keyPrefix, String cacheName) {
        this.keyPrefix = keyPrefix;
        this.cacheName = cacheName;
    }

    protected String buildKey(K bizKey) {
        return String.format("%s:{%s}:%s", keyPrefix, cacheName, getBizKeyStr(bizKey));
    }

    protected String getBizKeyStr(K bizKey) {
        return String.valueOf(bizKey);
    }
}
