//package com.yetcache.core.cache.flathash;
//
//import com.yetcache.core.cache.support.CacheValueHolder;
//import lombok.Data;
//
//import java.util.Map;
//
///**
// * @author walter.yan
// * @since 2025/7/9
// */
//@Data
//public class FlatHashAccessResult<T> {
//    private boolean success = true;
//    private String message;
//    private Throwable exception;
//
//    private T value;
//    private FlatHashCacheAccessTrace trace;
//
//    public FlatHashAccessResult() {
//        this.trace = new FlatHashCacheAccessTrace();
//    }
//
//    public static FlatHashAccessResult<Void> success() {
//        return new FlatHashAccessResult<>();
//    }
//
//    public static <F, V> FlatHashAccessResult<Map<F, V>> success(Map<F, V> map) {
//        FlatHashAccessResult<Map<F, V>> result = new FlatHashAccessResult<>();
//        result.setValue(map);
//        return result;
//    }
//
//    public static <F, V> FlatHashAccessResult<Map<F, V>> failRefresh(Throwable e) {
//        FlatHashAccessResult<Map<F, V>> result = new FlatHashAccessResult<>();
//        result.setSuccess(false);
//        result.setException(e);
//        return result;
//    }
//
//    public static <V> FlatHashAccessResult<CacheValueHolder<V>> failGet(Throwable e) {
//        FlatHashAccessResult<CacheValueHolder<V>> result = new FlatHashAccessResult<>();
//        result.setSuccess(false);
//        result.setException(e);
//        return result;
//    }
//
//
//    public static <V> FlatHashAccessResult<CacheValueHolder<V>> blocked() {
//        FlatHashAccessResult<CacheValueHolder<V>> result = new FlatHashAccessResult<>();
//        result.setTrace(FlatHashCacheAccessTrace.blocked());
//        return result;
//    }
//}
