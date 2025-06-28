package com.yetcache.core.kv;

/**
 * @author walter.yan
 * @since 2025/6/18
 */
public class CacheResult<K> {
    protected K bizKey;
    protected boolean localHit;
    protected boolean remoteHit;
    protected boolean missThenLoad;
}
