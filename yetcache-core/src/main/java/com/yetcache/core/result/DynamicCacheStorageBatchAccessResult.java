//package com.yetcache.core.result;
//
//import com.yetcache.core.cache.support.CacheValueHolder;
//import com.yetcache.core.cache.trace.HitTier;
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//
//import java.util.Map;
//
///**
// * @author walter.yan
// * @since 2025/7/25
// */
//@EqualsAndHashCode(callSuper = true)
//@Getter
//public final class DynamicCacheStorageBatchAccessResult<F, V> extends AbstractCacheStorageAccessResult<Map<F, CacheValueHolder<V>>> {
//
//    /**
//     * 命中的层级，对应方法：
//     * <li>batchGet</li>
//     */
//    private Map<F, HitTier> hitTiers;
//
//    /**
//     * 命中的层级，对应方法：
//     * <li>listAll</li>
//     */
//    private HitTier hitTier;
//
//    public DynamicCacheStorageBatchAccessResult(CacheOutcome outcome, Map<F, CacheValueHolder<V>> valueHolderMap,
//                                                Map<F, HitTier> hitTierMap) {
//        super(outcome, valueHolderMap);
//        this.hitTiers = hitTierMap;
//    }
//
//    public DynamicCacheStorageBatchAccessResult(CacheOutcome outcome, Map<F, CacheValueHolder<V>> valueHolderMap,
//                                                Map<F, HitTier> hitTierMap, Throwable error) {
//        super(outcome, valueHolderMap, error);
//        this.hitTiers = hitTierMap;
//    }
//
//    public DynamicCacheStorageBatchAccessResult(CacheOutcome outcome, Map<F, CacheValueHolder<V>> valueHolderMap,
//                                                HitTier hitTier) {
//        super(outcome, valueHolderMap);
//        this.hitTier = hitTier;
//    }
//
//    public DynamicCacheStorageBatchAccessResult(CacheOutcome outcome) {
//        super(outcome, null);
//    }
//
//    public DynamicCacheStorageBatchAccessResult(CacheOutcome outcome, Throwable error) {
//        super(outcome, null, error);
//    }
//
//    public static <F, V> DynamicCacheStorageBatchAccessResult<F, V> hit(Map<F, CacheValueHolder<V>> valueHolderMap,
//                                                                        Map<F, HitTier> hitTierMap) {
//        return new DynamicCacheStorageBatchAccessResult<>(CacheOutcome.HIT, valueHolderMap, hitTierMap);
//    }
//
//    public static <F, V> DynamicCacheStorageBatchAccessResult<F, V> hit(Map<F, CacheValueHolder<V>> valueHolderMap,
//                                                                        HitTier hitTier) {
//        return new DynamicCacheStorageBatchAccessResult<>(CacheOutcome.HIT, valueHolderMap, hitTier);
//    }
//
//    public static <F, V> DynamicCacheStorageBatchAccessResult<F, V> error(Throwable e) {
//        return new DynamicCacheStorageBatchAccessResult<>(CacheOutcome.FAIL, e);
//    }
//
//    public static <F, V> DynamicCacheStorageBatchAccessResult<F, V> miss() {
//        return new DynamicCacheStorageBatchAccessResult<>(CacheOutcome.MISS);
//    }
//
//    public static <F, V> DynamicCacheStorageBatchAccessResult<F, V> success() {
//        return new DynamicCacheStorageBatchAccessResult<>(CacheOutcome.SUCCESS);
//    }
//}
