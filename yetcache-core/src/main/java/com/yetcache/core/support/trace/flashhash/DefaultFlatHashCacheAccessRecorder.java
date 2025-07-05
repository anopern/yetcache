//package com.yetcache.core.support.trace.flashhash;
//
//
//import com.yetcache.core.support.trace.dynamichash.CacheAccessGetStatus;
//import com.yetcache.core.support.trace.dynamichash.SourceLoadStatus;
//import com.yetcache.core.context.CacheAccessContext;
//import com.yetcache.core.support.trace.CacheBatchAccessStatus;
//
//import java.util.Collection;
//import java.util.Collections;
//
///**
// * @author walter.yan
// * @since 2025/6/30
// */
//public class DefaultFlatHashCacheAccessRecorder<F> implements FlatHashCacheAccessRecorder<F> {
//    private boolean hasAnyFieldSuccess = false;
//    private boolean hasAnyFieldFail = false;
//    private boolean exceptionBeforeLoop = false;
//
//    @Override
//    public void recordStart() {
//        FlatHashCacheAccessTrace<F> trace = new FlatHashCacheAccessTrace<>();
//        trace.setBatchStatus(CacheBatchAccessStatus.NOT_EXECUTED);
//        CacheAccessContext.setFlatHashTrace(trace);
//        trace.setStartMills(System.currentTimeMillis());
//    }
//
//    @Override
//    public void recordStart(F bizField) {
//        recordStart(Collections.singletonList(bizField));
//    }
//
//    @Override
//    public void recordStart(Collection<F> bizFields) {
//        recordStart();
//        for (F bizField : bizFields) {
//            getTrace().getLocalStatusMap().put(bizField, CacheAccessGetStatus.PENDING);
//            getTrace().getRemoteStatusMap().put(bizField, CacheAccessGetStatus.PENDING);
//            getTrace().getLoadStatusMap().put(bizField, SourceLoadStatus.PENDING);
//        }
//    }
//
//    @Override
//    public void recordLocalPhysicalMiss(F bizField) {
//        getTrace().getLocalStatusMap().put(bizField, CacheAccessGetStatus.PHYSICAL_MISS);
//    }
//
//    @Override
//    public void recordLocalHit(F bizField) {
//        getTrace().getLocalStatusMap().put(bizField, CacheAccessGetStatus.HIT);
//    }
//
//    @Override
//    public void markLocalLogicExpired(F bizField) {
//        getTrace().getLocalStatusMap().put(bizField, CacheAccessGetStatus.LOGIC_EXPIRED);
//    }
//
//    @Override
//    public void recordLocalBlocked(F bizField) {
//        getTrace().getLocalStatusMap().put(bizField, CacheAccessGetStatus.BLOCKED);
//    }
//
//    @Override
//    public void recordRemoteBlocked(F bizField) {
//        getTrace().getRemoteStatusMap().put(bizField, CacheAccessGetStatus.BLOCKED);
//    }
//
//
//    @Override
//    public void recordRemoteHit(F bizField) {
//        getTrace().getRemoteStatusMap().put(bizField, CacheAccessGetStatus.HIT);
//    }
//
//    @Override
//    public void recordRemotePhysicalMiss(F bizField) {
//        getTrace().getRemoteStatusMap().put(bizField, CacheAccessGetStatus.PHYSICAL_MISS);
//    }
//
//    @Override
//    public void recordRemoteLogicExpired(F bizField) {
//        getTrace().getRemoteStatusMap().put(bizField, CacheAccessGetStatus.LOGIC_EXPIRED);
//    }
//
//    @Override
//    public void recordSourceLoaded(F bizField) {
//        getTrace().getLoadStatusMap().put(bizField, SourceLoadStatus.LOADED);
//        hasAnyFieldSuccess = true;
//    }
//
//    @Override
//    public void recordSourceLoadNoValue(F bizField) {
//        getTrace().getLoadStatusMap().put(bizField, SourceLoadStatus.NO_VALUE);
//    }
//
//    @Override
//    public void recordSourceLoadFailed(F bizField) {
//        getTrace().getLoadStatusMap().put(bizField, SourceLoadStatus.ERROR);
//        hasAnyFieldFail = true;
//    }
//
//    @Override
//    public void recordExceptionBeforeLoop() {
//        getTrace().setBatchStatus(CacheBatchAccessStatus.EXCEPTION_BEFORE_LOOP);
//    }
//
//    @Override
//    public void recordSourceLoadAllNoValue() {
//        getTrace().setBatchStatus(CacheBatchAccessStatus.ALL_FAILED);
//    }
//
//    @Override
//    public void recordEnd() {
//        // 若 trace 中已被外部 catch 显式设置（如 EXCEPTION_BEFORE_LOOP），则不覆盖
//        if (getTrace().getBatchStatus() == null || getTrace().getBatchStatus() == CacheBatchAccessStatus.NOT_EXECUTED) {
//            if (hasAnyFieldSuccess && hasAnyFieldFail) {
//                getTrace().setBatchStatus(CacheBatchAccessStatus.PARTIAL_SUCCESS);
//            } else if (hasAnyFieldSuccess) {
//                getTrace().setBatchStatus(CacheBatchAccessStatus.ALL_SUCCESS);
//            } else {
//                getTrace().setBatchStatus(CacheBatchAccessStatus.ALL_FAILED);
//            }
//        }
//
//        getTrace().setEndMills(System.currentTimeMillis());
//    }
//
//    private FlatHashCacheAccessTrace<F> getTrace() {
//        return CacheAccessContext.getFlatHashTrace();
//    }
//}
