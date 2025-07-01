package com.yetcache.agent;

import com.yetcache.core.cache.flathash.FlatHashCache;
import com.yetcache.core.cache.loader.FlatHashCacheLoader;
import com.yetcache.core.cache.manager.FlatHashCacheManager;
import com.yetcache.core.context.CacheAccessContext;
import com.yetcache.core.support.tenant.TenantProvider;
import com.yetcache.core.support.trace.CacheBatchAccessStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author walter.yan
 * @since 2025/6/28
 */

@EqualsAndHashCode(callSuper = true)
public abstract class BaseConfigCacheAgent<F, V> extends BaseCacheAgent {
    protected FlatHashCache<F, V> delegate;

    public BaseConfigCacheAgent(RedissonClient rClient, TenantProvider tenantProvider, FlatHashCache<F, V> delegate) {
        super(rClient, tenantProvider);
        this.delegate = delegate;
    }
}
