package com.yetcache.core.cache;

import com.yetcache.core.cache.result.BaseCacheResult;
import com.yetcache.core.cache.result.CacheAccessStatus;
import com.yetcache.core.protect.CaffeinePenetrationProtectCache;
import com.yetcache.core.protect.RedisPenetrationProtectCache;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
@Data
public abstract class AbstractMultiTierCache<K> {
    protected String cacheName;
    protected CaffeinePenetrationProtectCache<K> localPpCache;
    protected RedisPenetrationProtectCache<K> remotePpCache;

    protected <T extends BaseCacheResult<K, T>> boolean tryLocalBlock(K key, T getResult) {
        if (localPpCache != null && localPpCache.isBlocked(key)) {
            getResult.setLocalStatus(CacheAccessStatus.BLOCKED);
            getResult.end();
            return true;
        }
        return false;
    }

    protected <T extends BaseCacheResult<K, T>> boolean tryRemoteBlock(K key, T getResult) {
        if (remotePpCache != null && remotePpCache.isBlocked(key)) {
            getResult.setRemoteStatus(CacheAccessStatus.BLOCKED);
            getResult.end();
            if (null != localPpCache) {
                localPpCache.markMiss(key);
            }
            return true;
        }
        return false;
    }

}
