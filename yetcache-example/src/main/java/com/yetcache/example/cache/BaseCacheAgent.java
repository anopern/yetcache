package com.yetcache.example.cache;

import com.yetcache.core.support.tenant.TenantProvider;
import lombok.Data;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @author walter.yan
 * @since 2025/7/1
 */
@Data
public abstract class BaseCacheAgent {
    @Autowired
    protected RedissonClient rClient;
    @Autowired(required = false)
    private TenantProvider tenantProvider;

    @PostConstruct
    public void init() {
        createCache();
    }

    protected abstract void createCache();
}
