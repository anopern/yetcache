//package com.yetcache.agent;
//
//import com.yetcache.core.cache.kv.KVCache;
//import com.yetcache.core.support.tenant.TenantProvider;
//import lombok.EqualsAndHashCode;
//import org.redisson.api.RedissonClient;
//
///**
// * @author walter.yan
// * @since 2025/6/28
// */
//
//@EqualsAndHashCode(callSuper = true)
//public abstract class BaseKVCacheAgent<K, V> extends BaseCacheAgent {
//    protected KVCache<K, V> delegate;
//
//    public BaseKVCacheAgent(RedissonClient rClient, TenantProvider tenantProvider, KVCache<K, V> delegate) {
//        super(rClient, tenantProvider);
//        this.delegate = delegate;
//    }
//}
