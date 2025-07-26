//package com.yetcache.agent.result;
//
//import com.yetcache.core.cache.support.CacheValueHolder;
//import com.yetcache.core.cache.trace.HitTier;
//import com.yetcache.core.result.CacheAccessTrace;
//import com.yetcache.core.result.CacheOutcome;
//
//import java.util.Map;
//
///**
// * @author walter.yan
// * @since 2025/7/13
// */
//public final class ConfigCacheAgentBatchAccessResult<F, V>
//        extends AbstractCacheAgentResult<Map<F, CacheValueHolder<V>>> {
//    private ConfigCacheAgentBatchAccessResult(String cacheName,
//                                              CacheOutcome outcome,
//                                              Map<F, CacheValueHolder<V>> value,
//                                              Throwable e) {
//        super(cacheName, outcome, value, e);
//    }
//
//
//    public static <F, V> ConfigCacheAgentBatchAccessResult<F, V> success(String cacheName) {
//        return new ConfigCacheAgentBatchAccessResult<>(
//                cacheName,
//                CacheOutcome.SUCCESS,
//                null,
//                null);
//    }
//
//    /**
//     * 写类成功（无返回数据）
//     */
//    public static <F, V> ConfigCacheAgentBatchAccessResult<F, V> hit(String cacheName,
//                                                                     Map<F, CacheValueHolder<V>> valueHolderMap) {
//        return new ConfigCacheAgentBatchAccessResult<>(cacheName, CacheOutcome.HIT, valueHolderMap, null);
//    }
//
//    public static <F, V> ConfigCacheAgentBatchAccessResult<F, V> fail(String cacheName, Throwable ex) {
//        return new ConfigCacheAgentBatchAccessResult<>(
//                CacheOutcome.FAIL,
//                null,
//                null,
//                CacheAccessTrace.start().fail(ex),
//                cacheName
//        );
//    }
//
//    public static <F, V> ConfigCacheAgentBatchAccessResult<F, V> miss(String cacheName) {
//        return new ConfigCacheAgentBatchAccessResult<>(
//                CacheOutcome.MISS,
//                null,
//                null,
//                CacheAccessTrace.start().success(),
//                cacheName
//        );
//    }
//
//    /**
//     * 因限流/阻断被拒绝。
//     */
//    public static <F, V> ConfigCacheAgentBatchAccessResult<F, V> flatHashBlock(String cacheName, String reason) {
//        return new ConfigCacheAgentBatchAccessResult<>(
//                CacheOutcome.BLOCK,
//                null,
//                null,
//                CacheAccessTrace.start().block(reason),
//                cacheName
//        );
//    }
//}