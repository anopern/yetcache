//package com.yetcache.agent.protect;
//
//import com.yetcache.agent.interceptor.CacheAccessKey;
//
///**
// * @author walter.yan
// * @since 2025/7/16
// */
//public interface PenetrationProtector {
//
//    /**
//     * 判断是否需要拦截本次访问（即短路，跳过回源）
//     */
//    boolean isMarkedAsNull(CacheAccessKey key);
//
//    /**
//     * 标记该 key 为穿透失败（业务 miss，但不为空）
//     * 用于 future fallback、空值回源前提分析等
//     */
//    void markAsNull(CacheAccessKey key);
//}
//
