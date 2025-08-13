//package com.yetcache.agent.core;
//
//import com.yetcache.core.cache.support.CacheValueHolder;
//
//import java.util.Collections;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * @author walter.yan
// * @since 2025/7/15
// */
//public final class CacheValueHolderHelper {
//    private CacheValueHolderHelper() {
//    }
//
//    public static <F, V> Map<F, CacheValueHolder<V>> wrapAsHolderMap(Map<F, V> valueMap) {
//        if (valueMap == null || valueMap.isEmpty()) return Collections.emptyMap();
//
//        return valueMap.entrySet().stream()
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        e -> CacheValueHolder.wrap(e.getValue(), 0)
//                ));
//    }
//}
