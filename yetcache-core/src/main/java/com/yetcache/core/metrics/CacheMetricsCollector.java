//package com.yetcache.core.metrics;
//
//import com.yetcache.core.support.trace.dynamichash.CacheAccessGetStatus;
//import io.micrometer.core.instrument.Counter;
//import io.micrometer.core.instrument.MeterRegistry;
//import lombok.NoArgsConstructor;
//import org.springframework.stereotype.Component;
//
///**
// * @author walter.yan
// * @since 2025/7/3
// */
//@Component
//@NoArgsConstructor
//public class CacheMetricsCollector {
//    private Counter cacheAccessCounter;
//
//    public CacheMetricsCollector(Counter cacheAccessCounter) {
//        this.cacheAccessCounter = cacheAccessCounter;
//    }
//
//    public CacheMetricsCollector(MeterRegistry registry) {
//        this.cacheAccessCounter = Counter.builder("cache_access_total")
//                .description("缓存访问总量")
//                .tags("cache", "tier", "status")
//                .register(registry);
//    }
//
//    public void inc(String cacheName, HitTier tier, CacheAccessGetStatus status) {
//        cacheAccessCounter
//                .count()
//    }
//}
