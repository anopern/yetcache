//package com.yetcache.agent;
//
//import lombok.Data;
//import org.redisson.api.RedissonClient;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import javax.annotation.PostConstruct;
//
///**
// * @author walter.yan
// * @since 2025/7/1
// */
//@Data
//public abstract class BaseCacheAgent {
//    protected RedissonClient rClient;
//    private TenantProvider tenantProvider;
//
//    public BaseCacheAgent(RedissonClient rClient, TenantProvider tenantProvider) {
//        this.rClient = rClient;
//        this.tenantProvider = tenantProvider;
//    }
//
//    public abstract String getCacheName();
//}
