//package com.yetcache.example.cache.service;
//
//import cn.hutool.core.collection.CollUtil;
//import com.yetcache.core.support.CacheValueHolder;
//import com.yetcache.core.result.BaseCacheResult;
//import com.yetcache.core.result.HitLevel;
//import com.yetcache.example.entity.StockHoldInfo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * @author walter.yan
// * @since 2025/7/15
// */
//@Component
//public final class StockHoldInfoCacheService {
//    private final BaseHashCacheAgent stockHoldInfoCacheAgent;
//
//    @Autowired
//    public StockHoldInfoCacheService(BaseHashCacheAgent stockHoldInfoCacheAgent) {
//        this.stockHoldInfoCacheAgent = stockHoldInfoCacheAgent;
//    }
//
//    public Optional<StockHoldInfo> get(String fundAccount, Long id) {
//        BaseCacheResult<CacheValueHolder<StockHoldInfo>> result = stockHoldInfoCacheAgent.get(fundAccount, id);
//        if (result.isSuccess() && HitLevel.NONE != result.hitLevelInfo().hitLevel()) {
//            CacheValueHolder<StockHoldInfo> valueHolder = result.value();
//            return Optional.of(valueHolder.getValue());
//        }
//        return Optional.empty();
//    }
//
//    public List<StockHoldInfo> batchGet(String fundAccount, List<Long> ids) {
//        BaseCacheResult<Map<Long, CacheValueHolder<StockHoldInfo>>> result =
//                stockHoldInfoCacheAgent.batchGet(fundAccount, Arrays.asList(ids.toArray()));
//        if (result.isSuccess()) {
//            Map<Long, CacheValueHolder<StockHoldInfo>> valueHolderMap = result.value();
//            if (CollUtil.isNotEmpty(valueHolderMap)) {
//                return valueHolderMap.values().stream()
//                        .map(CacheValueHolder::getValue)
//                        .collect(Collectors.toList());
//            }
//        }
//        return Collections.emptyList();
//    }
//
//    public void remove(String fundAccount, Long id) {
//        BaseCacheResult<Void> removeResult = stockHoldInfoCacheAgent.remove(fundAccount, id);
//        if (!removeResult.isSuccess()) {
//            throw new RuntimeException(removeResult.message());
//        }
//    }
//
//    public void batchRemove(String fundAccount, List<Long> ids) {
//        BaseCacheResult<Void> batchRemoveResult = stockHoldInfoCacheAgent.batchRemove(fundAccount, ids);
//        if (!batchRemoveResult.isSuccess()) {
//            throw new RuntimeException(batchRemoveResult.message());
//        }
//    }
//
//    public void refresh(String fundAccount, Long id) {
//        BaseCacheResult<Void> refreshResult = stockHoldInfoCacheAgent.refresh(fundAccount, id);
//        if (!refreshResult.isSuccess()) {
//            throw new RuntimeException(refreshResult.message());
//        }
//    }
//
//    public void batchRefresh(String fundAccount, List<Long> ids) {
//        BaseCacheResult<Void> refreshResult = stockHoldInfoCacheAgent.batchRefresh(fundAccount, ids);
//        if (!refreshResult.isSuccess()) {
//            throw new RuntimeException(refreshResult.message());
//        }
//    }
//
////    public List<StockHoldInfo> listAll(String fundAccount) {
////        return listAll(fundAccount, false);
////    }
////
////    public List<StockHoldInfo> listAll(String fundAccount, boolean forceRefresh) {
////        CacheAccessContext.setForceRefresh(forceRefresh);
////        DynamicHashCacheAgentResult<String, Long, StockHoldInfo> result = stockHoldInfoCacheAgent.listAll(fundAccount);
////        return result.toValueList();
////    }
////
////    public boolean refreshAll(String fundAccount) {
////        DynamicHashCacheAgentResult<String, Long, StockHoldInfo> result = stockHoldInfoCacheAgent.refreshAll(fundAccount);
////        return result.isSuccess();
////    }
//}
