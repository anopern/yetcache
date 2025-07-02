package com.yetcache.core.cache;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.support.field.FieldConverter;
import com.yetcache.core.support.result.CacheLookupResult;
import com.yetcache.core.util.CacheKeyUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author walter.yan
 * @since 2025/7/2
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AbstractMultiTierHashCache<F, V> extends AbstractMultiTierCache {
    protected CaffeineHashCache<V> localCache;
    protected RedisHashCache<V> remoteCache;
    protected FieldConverter<F> fieldConverter;

    protected CacheLookupResult<V> tryLocalGet(String key, String field) {
        if (localCache == null) return null;
        return buildLookupResult(localCache.getIfPresent(key, field));
    }

    protected CacheLookupResult<V> tryRemoteGet(String key, String field) {
        if (remoteCache == null) return null;
        return buildLookupResult(remoteCache.getIfPresent(key, field));
    }

    private CacheLookupResult<V> buildLookupResult(CacheValueHolder<V> holder) {
        CacheLookupResult<V> result = new CacheLookupResult<>();
        if (holder == null) {
            result.physicalMiss();
        } else if (holder.isNotLogicExpired()) {
            result.hit();
            result.setValueHolder(holder);
        } else {
            result.logicalExpired();
        }
        return result;
    }

    protected void markPenetrationProtect(String key, String field) {
        String logicKey = CacheKeyUtil.joinLogicalKey(key, field);
        if (localPpCache != null) {
            localPpCache.markMiss(logicKey);
        }
        if (remotePpCache != null) {
            remotePpCache.markMiss(logicKey);
        }
    }

}
