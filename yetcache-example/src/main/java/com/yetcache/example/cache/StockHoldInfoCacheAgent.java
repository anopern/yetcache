package com.yetcache.example.cache;

import com.yetcache.agent.BaseDynamicHashCacheAgent;
import com.yetcache.core.cache.dynamichash.DynamicHashCache;
import com.yetcache.core.support.tenant.TenantProvider;
import com.yetcache.example.entity.StockHoldInfo;
import com.yetcache.example.enums.EnumCaches;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

/**
 * @author walter.yan
 * @since 2025/7/2
 */
@Slf4j
public class StockHoldInfoCacheAgent extends BaseDynamicHashCacheAgent<String, Long, StockHoldInfo> {
    public StockHoldInfoCacheAgent(RedissonClient rClient, TenantProvider tenantProvider,
                                   DynamicHashCache<String, Long, StockHoldInfo> delegate) {
        super(rClient, tenantProvider, delegate);
    }

    public StockHoldInfo getById(String fundAccount, Long id) {
        return delegate.get(fundAccount, id);
    }

    @Override
    public String getCacheName() {
        return EnumCaches.STOCK_HOLD_INFO_CACHE.getName();
    }
}
