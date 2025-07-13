//package com.yetcache.core.cache.manager;
//
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
//public final class KVCacheRegistry {
//    private final Map<String, MultiTierKVCache<?, ?>> registry = new ConcurrentHashMap<>();
//
//    public MultiTierKVCache<?, ?> get(String name) {
//        return registry.get(name);
//    }
//
//    public boolean contains(String name) {
//        return registry.containsKey(name);
//    }
//
//    public void register(String name, MultiTierKVCache<?, ?> cache) {
//        registry.put(name, cache);
//    }
//
//    public void clearRegistry() {
//        registry.clear();
//        log.warn("KVCache registry cleared");
//    }
//
//}
