//package com.yetcache.core.support.trace.flashhash;
//
//import com.yetcache.core.support.trace.dynamichash.CacheAccessGetStatus;
//import com.yetcache.core.support.trace.dynamichash.SourceLoadStatus;
//import com.yetcache.core.support.trace.dynamichash.BaseCacheAccessTrace;
//import com.yetcache.core.support.trace.CacheBatchAccessStatus;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author walter.yan
// * @since 2025/6/30
// */
//@EqualsAndHashCode(callSuper = true)
//@Data
//public class FlatHashCacheAccessTrace<F> extends BaseCacheAccessTrace {
//    private CacheBatchAccessStatus batchStatus;
//
//    protected Map<F, CacheAccessGetStatus> localStatusMap = new HashMap<>();
//    protected Map<F, CacheAccessGetStatus> remoteStatusMap = new HashMap<>();
//    protected Map<F, SourceLoadStatus> loadStatusMap = new HashMap<>();
//}
