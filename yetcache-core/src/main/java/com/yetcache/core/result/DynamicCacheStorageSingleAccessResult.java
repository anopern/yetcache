//package com.yetcache.core.result;
//
//import com.yetcache.core.cache.support.CacheValueHolder;
//import com.yetcache.core.cache.trace.HitTier;
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//
///**
// * @author walter.yan
// * @since 2025/7/25
// */
//@EqualsAndHashCode(callSuper = true)
//@Getter
//public final class DynamicCacheStorageSingleAccessResult<V> extends AbstractCacheStorageAccessResult<CacheValueHolder<V>> {
//    private final HitTier hitTier;
//
//    public DynamicCacheStorageSingleAccessResult(CacheOutcome outcome, CacheValueHolder<V> valueHolder,
//                                                 HitTier hitTier) {
//        super(outcome, valueHolder);
//        this.hitTier = hitTier;
//    }
//
//    public DynamicCacheStorageSingleAccessResult(CacheOutcome outcome, CacheValueHolder<V> valueHolder,
//                                                 HitTier hitTier, Throwable error) {
//        super(outcome, valueHolder, error);
//        this.hitTier = hitTier;
//    }
//
//    public static <V> DynamicCacheStorageSingleAccessResult<V> hit(CacheValueHolder<V> valueHolder, HitTier hitTier) {
//        return new DynamicCacheStorageSingleAccessResult<>(CacheOutcome.HIT, valueHolder, hitTier);
//    }
//
//    public static <V> DynamicCacheStorageSingleAccessResult<V> miss() {
//        return new DynamicCacheStorageSingleAccessResult<>(CacheOutcome.MISS, null, null);
//    }
//
//    public static <V> DynamicCacheStorageSingleAccessResult<V> success() {
//        return new DynamicCacheStorageSingleAccessResult<>(CacheOutcome.SUCCESS, null, null);
//    }
//}
