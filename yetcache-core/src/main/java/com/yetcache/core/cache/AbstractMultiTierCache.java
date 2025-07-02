package com.yetcache.core.cache;

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
}
