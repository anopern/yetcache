//package com.yetcache.core.cache.dynamichash;
//
//import com.yetcache.core.cache.support.CacheValueHolder;
//import com.yetcache.core.cache.trace.HitTier;
//import com.yetcache.core.result.AbstractStorageResult;
//import com.yetcache.core.result.CacheAccessTrace;
//import com.yetcache.core.result.CacheOutcome;
//
//import java.io.Serializable;
//import java.util.Collections;
//import java.util.Map;
//import java.util.Objects;
//
///**
// * Result DTO specialised for Flat-Hash read operations.
// *
// * @author walter.yan
// * @since 2025/7/13
// */
//public final class DynamicHashStorageResult<K, F, V>
//        extends AbstractStorageResult<Map<K, Map<F, CacheValueHolder<V>>>>
//        implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//    private DynamicHashStorageResult(CacheOutcome outcome,
//                                     Map<K, Map<F, CacheValueHolder<V>>> value,
//                                     CacheAccessTrace trace,
//                                     HitTier tier,
//                                     Boolean fromSource) {
//        super(outcome, value, trace, tier, fromSource);
//    }
//
//    private DynamicHashStorageResult(CacheOutcome outcome,
//                                     Map<K, Map<F, CacheValueHolder<V>>> value,
//                                     CacheAccessTrace trace) {
//        super(outcome, value, trace, null, null);
//    }
//
//    private DynamicHashStorageResult(CacheOutcome outcome,
//                                     Map<K, Map<F, CacheValueHolder<V>>> value,
//                                     CacheAccessTrace trace,
//                                     HitTier tier) {
//        super(outcome, value, trace, tier, null);
//    }
//
//    // 命中（返回完整结构）
//    public static <K, F, V> DynamicHashStorageResult<K, F, V> hit(
//            K bizKey, F bizField, CacheValueHolder<V> value, HitTier tier) {
//        Map<K, Map<F, CacheValueHolder<V>>> map = Map.of(bizKey, Map.of(bizField, value));
//        return new DynamicHashStorageResult<>(
//                CacheOutcome.HIT,
//                map,
//                CacheAccessTrace.start().success(),
//                tier,
//                false
//        );
//    }
//
//    public static <K, F, V> DynamicHashStorageResult<K, F, V> hit(
//            Map<K, Map<F, CacheValueHolder<V>>> value, HitTier tier) {
//        return new DynamicHashStorageResult<>(
//                CacheOutcome.HIT,
//                Collections.unmodifiableMap(Objects.requireNonNull(value)),
//                CacheAccessTrace.start().success(),
//                tier,
//                false
//        );
//    }
//
//    public static <K, F, V> DynamicHashStorageResult<K, F, V> miss() {
//        return new DynamicHashStorageResult<>(
//                CacheOutcome.MISS,
//                Collections.emptyMap(),
//                CacheAccessTrace.start().success());
//    }
//
//    public static <K, F, V> DynamicHashStorageResult<K, F, V> hitHolderMap(
//            K bizKey, Map<F, CacheValueHolder<V>> holderMap, HitTier hitTier) {
//        return new DynamicHashStorageResult<>(
//                CacheOutcome.HIT,
//                Map.of(bizKey, holderMap),
//                CacheAccessTrace.start().success(),
//                hitTier);
//    }
//
//    public static <K, F, V> DynamicHashStorageResult<K, F, V> putSuccess(
//            K bizKey, F bizField, CacheValueHolder<V> holder) {
//        return new DynamicHashStorageResult<>(
//                CacheOutcome.SUCCESS,
//                Map.of(bizKey, Map.of(bizField, holder)),
//                CacheAccessTrace.start().success());
//    }
//
//    public static <K, F, V> DynamicHashStorageResult<K, F, V> putAllSuccess(
//            K bizKey, Map<F, CacheValueHolder<V>> holderMap) {
//        return new DynamicHashStorageResult<>(
//                CacheOutcome.SUCCESS,
//                Map.of(bizKey, holderMap),
//                CacheAccessTrace.start().success());
//    }
//
//    public static <K, F, V> DynamicHashStorageResult<K, F, V> invalidateSuccess() {
//        return new DynamicHashStorageResult<>(
//                CacheOutcome.SUCCESS,
//                Collections.emptyMap(),
//                CacheAccessTrace.start().success());
//    }
//    public static <K, F, V> DynamicHashStorageResult<K, F, V> invalidateAllSuccess() {
//        return new DynamicHashStorageResult<>(
//                CacheOutcome.SUCCESS,
//                Collections.emptyMap(),
//                CacheAccessTrace.start().success());
//    }
//
//    public static <K, F, V> DynamicHashStorageResult<K, F, V> fail(Throwable ex) {
//        return new DynamicHashStorageResult<>(
//                CacheOutcome.FAIL,
//                Collections.emptyMap(),
//                CacheAccessTrace.start().fail(ex),
//                HitTier.NONE,
//                false
//        );
//    }
//
//    public static <K, F, V> DynamicHashStorageResult<K, F, V> success() {
//        return new DynamicHashStorageResult<>(
//                CacheOutcome.SUCCESS,
//                Collections.emptyMap(),
//                CacheAccessTrace.start().success(),
//                HitTier.NONE,
//                false
//        );
//    }
//
//    @Override
//    public DynamicHashStorageResult<K, F, V> withTrace(CacheAccessTrace trace) {
//        return new DynamicHashStorageResult<>(outcome(), value(), trace, hitTier(), fromSource());
//    }
//}
