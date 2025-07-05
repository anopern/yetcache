//package com.yetcache.core.support.trace.dynamichash;
//
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
//public class DynamicHashCacheBatchAccessTrace<K, F> extends BaseCacheAccessTrace {
//    private CacheBatchAccessStatus batchStatus;
//    protected Map<K, Map<F, CacheAccessGetStatus>> localStatusMap = new HashMap<>();
//    protected Map<K, Map<F, CacheAccessGetStatus>> remoteStatusMap = new HashMap<>();
//    protected Map<K, Map<F, SourceLoadStatus>> loadStatusMap = new HashMap<>();
//}
