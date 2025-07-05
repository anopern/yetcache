//package com.yetcache.core.support.result;
//
//import com.yetcache.core.support.trace.dynamichash.SourceLoadStatus;
//import com.yetcache.core.cache.support.CacheValueHolder;
//import lombok.Data;
//
///**
// * @author walter.yan
// * @since 2025/6/30
// */
//@Data
//public class CacheLoadResult<V> {
//    private CacheValueHolder<V> valueHolder;
//    private SourceLoadStatus status;
//    private Throwable exception;
//
//    public CacheLoadResult() {
//    }
//
//    public void noValue() {
//        status = SourceLoadStatus.NO_VALUE;
//    }
//
//    public void loaded() {
//        status = SourceLoadStatus.LOADED;
//    }
//
//    public void error() {
//        status = SourceLoadStatus.ERROR;
//    }
//
//    public boolean isLoaded() {
//        return status == SourceLoadStatus.LOADED;
//    }
//
//    public boolean isNoValue() {
//        return status == SourceLoadStatus.NO_VALUE;
//    }
//
//    public boolean isError() {
//        return status == SourceLoadStatus.ERROR;
//    }
//}
