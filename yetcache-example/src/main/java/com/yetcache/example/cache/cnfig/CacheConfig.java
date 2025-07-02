package com.yetcache.example.cache.cnfig;

import com.yetcache.core.cache.dynamichash.MultiTierDynamicHashCache;
import com.yetcache.core.cache.flathash.MultiTierFlatHashCache;
import com.yetcache.core.cache.kv.MultiTierKVCache;
import com.yetcache.core.cache.manager.DynamicHashCacheManager;
import com.yetcache.core.cache.manager.FlatHashCacheManager;
import com.yetcache.core.cache.manager.KVCacheManager;
import com.yetcache.core.support.tenant.TenantProvider;
import com.yetcache.example.cache.ConfigCommonInfoCacheAgent;
import com.yetcache.example.cache.StockHoldInfoCacheAgent;
import com.yetcache.example.cache.UserCacheAgent;
import com.yetcache.example.entity.ConfigCommonInfo;
import com.yetcache.example.entity.StockHoldInfo;
import com.yetcache.example.entity.User;
import com.yetcache.example.enums.EnumCaches;
import com.yetcache.example.service.loader.ConfigCommonInfoCacheLoader;
import com.yetcache.example.service.loader.IdKeyUserCacheLoader;
import com.yetcache.example.service.loader.StockHoldInfoCacheLoader;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author walter.yan
 * @since 2025/7/2
 */
@Configuration
public class CacheConfig {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private TenantProvider tenantProvider;

    @Autowired
    private KVCacheManager kvCacheManager;
    @Autowired
    private FlatHashCacheManager flatHashCacheManager;
    @Autowired
    private DynamicHashCacheManager dynamicHashCacheManager;


//    @Bean
//    public ConfigCommonInfoCacheAgent configCommonInfoCacheAgent(ConfigCommonInfoCacheLoader cacheLoader) {
//        MultiTierFlatHashCache<String, ConfigCommonInfo> cache = flatHashCacheManager.create(
//                EnumCaches.CONFIG_COMMON_INFO_CACHE.getName(), redissonClient, cacheLoader);
//        return new ConfigCommonInfoCacheAgent(redissonClient, tenantProvider, cache);
//    }
//
//    @Bean
//    public UserCacheAgent userCacheAgent(IdKeyUserCacheLoader cacheLoader) {
//        MultiTierKVCache<Long, User> cache = kvCacheManager.create(
//                EnumCaches.USER_ID_KEY_CACHE.getName(), redissonClient, cacheLoader);
//        return new UserCacheAgent(redissonClient, tenantProvider, cache);
//    }

    @Bean
    public StockHoldInfoCacheAgent stockHoldInfoCacheAgent(StockHoldInfoCacheLoader cacheLoader) {
        MultiTierDynamicHashCache<String, Long, StockHoldInfo> cache = dynamicHashCacheManager.create(
                EnumCaches.STOCK_HOLD_INFO_CACHE.getName(), redissonClient, cacheLoader);
        return new StockHoldInfoCacheAgent(redissonClient, tenantProvider, cache);
    }
}
