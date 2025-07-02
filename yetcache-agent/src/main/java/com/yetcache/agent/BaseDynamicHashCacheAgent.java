package com.yetcache.agent;

import com.yetcache.core.cache.dynamichash.DynamicHashCache;
import com.yetcache.core.cache.flathash.FlatHashCache;
import com.yetcache.core.support.tenant.TenantProvider;
import lombok.EqualsAndHashCode;
import org.redisson.api.RedissonClient;

/**
 * @author walter.yan
 * @since 2025/6/28
 */

@EqualsAndHashCode(callSuper = true)
public abstract class BaseDynamicHashCacheAgent<K, F, V> extends BaseCacheAgent {
    protected DynamicHashCache<K, F, V> delegate;

    public BaseDynamicHashCacheAgent(RedissonClient rClient,
                                     TenantProvider tenantProvider,
                                     DynamicHashCache<K, F, V> delegate) {
        super(rClient, tenantProvider);
        this.delegate = delegate;
    }
}
