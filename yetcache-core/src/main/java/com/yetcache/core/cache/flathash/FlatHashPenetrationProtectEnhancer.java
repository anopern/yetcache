//package com.yetcache.core.cache.flathash;
//
//import com.yetcache.core.config.PenetrationProtectConfig;
//import com.yetcache.core.config.flathash.FlatHashCacheEnhanceConfig;
//import com.yetcache.core.support.field.FieldConverter;
//
///**
// * @author walter.yan
// * @since 2025/7/12
// */
//public class FlatHashPenetrationProtectEnhancer<F, V> implements MultiTierFlatHashCacheBehaviorEnhancer<F, V>{
//    private final String cacheName;
//    private final FieldConverter<F> fieldConverter;
//
//    public FlatHashPenetrationProtectEnhancer(String cacheName, FieldConverter<F> fieldConverter) {
//        this.cacheName = cacheName;
//        this.fieldConverter = fieldConverter;
//    }
//
//    @Override
//    public MultiTierFlatHashCache<F, V> enhance(MultiTierFlatHashCache<F, V> origin,
//                                                FlatHashCacheEnhanceConfig config) {
//        PenetrationProtectConfig protectConfig = config.getPenetrationProtect();
//        if (protectConfig == null || !Boolean.TRUE.equals(protectConfig.getEnabled())) {
//            return origin;
//        }
//
//        return new FlatHashPenetrationProtectDecorator<>(cacheName, origin, protectConfig, fieldConverter);
//    }
//}
