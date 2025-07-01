package com.yetcache.example.cache;


import com.yetcache.agent.BaseConfigCacheAgent;
import com.yetcache.core.cache.flathash.FlatHashCache;
import com.yetcache.core.support.tenant.TenantProvider;
import com.yetcache.example.entity.ConfigCommonInfo;
import com.yetcache.example.enums.EnumCaches;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

/**
 * @author walter.yan
 * @since 2025/6/30
 */
@Slf4j
public class ConfigCommonInfoCacheAgent extends BaseConfigCacheAgent<String, ConfigCommonInfo> {
    public ConfigCommonInfoCacheAgent(RedissonClient rClient, TenantProvider tenantProvider,
                                      FlatHashCache<String, ConfigCommonInfo> delegate) {
        super(rClient, tenantProvider, delegate);
    }

    public ConfigCommonInfo getByCode(String code) {
        return delegate.get(code);
    }

    @Override
    public String getCacheName() {
        return EnumCaches.CONFIG_COMMON_INFO_CACHE.getName();
    }
}
