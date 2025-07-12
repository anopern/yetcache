//package com.yetcache.example.cache;
//
//import com.yetcache.agent.BaseKVCacheAgent;
//import com.yetcache.core.cache.kv.MultiTierKVCache;
//import com.yetcache.core.support.tenant.TenantProvider;
//import com.yetcache.example.entity.User;
//import com.yetcache.example.enums.EnumCaches;
//import org.redisson.api.RedissonClient;
//import org.springframework.stereotype.Component;
//
///**
// * @author walter.yan
// * @since 2025/6/28
// */
//public final class UserCacheAgent extends BaseKVCacheAgent<Long, User> {
//    public UserCacheAgent(RedissonClient rClient, TenantProvider tenantProvider, MultiTierKVCache<Long, User> cache) {
//        super(rClient, tenantProvider, cache);
//    }
//
//    public User getById(Long id) {
//        return delegate.get(id);
//    }
//
//    @Override
//    public String getCacheName() {
//        return EnumCaches.USER_ID_KEY_CACHE.getName();
//    }
//}
