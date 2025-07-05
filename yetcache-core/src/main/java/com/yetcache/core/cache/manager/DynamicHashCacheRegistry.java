//package com.yetcache.core.cache.manager;
//
//import com.yetcache.core.cache.dynamichash.MultiTierDynamicHashCache;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author walter.yan
// * @since 2025/6/28
// */
//@Slf4j
//@Component
//public final class DynamicHashCacheRegistry {
//    private final Map<String, MultiTierDynamicHashCache<?, ?, ?>> registry = new ConcurrentHashMap<>();
//
//    public MultiTierDynamicHashCache<?, ?, ?> get(String name) {
//        return registry.get(name);
//    }
//
//    public boolean contains(String name) {
//        return registry.containsKey(name);
//    }
//
//    public void register(String name, MultiTierDynamicHashCache<?, ?,?> cache) {
//        registry.put(name, cache);
//    }
//
//    public void clearRegistry() {
//        registry.clear();
//        log.warn("MultiTierDynamicHashCache registry cleared");
//    }
//
//}
