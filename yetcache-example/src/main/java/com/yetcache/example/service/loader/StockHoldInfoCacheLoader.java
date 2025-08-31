//package com.yetcache.example.service.loader;
//
//import cn.hutool.core.collection.CollUtil;
//import com.yetcache.agent.core.structure.hash.AbstractHashCacheLoader;
//import com.yetcache.agent.core.structure.hash.HashCacheBatchLoadCommand;
//import com.yetcache.agent.core.structure.hash.HashCacheLoadCommand;
//import com.yetcache.core.result.BaseCacheResult;
//import com.yetcache.core.result.CacheResult;
//import com.yetcache.example.domain.entity.StockHoldInfo;
//import com.yetcache.example.service.IStockHoldInfoService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * @author walter.yan
// * @since 2025/7/2
// */
//@Component
//public class StockHoldInfoCacheLoader extends AbstractHashCacheLoader {
//    @Autowired
//    private IStockHoldInfoService stockHoldInfoService;
//
//    @Override
//    public String getComponentName() {
//        return "stock-hold-info-cache-loader";
//    }
//
//    @Override
//    public <K, F> CacheResult load(HashCacheLoadCommand<K, F> cmd) {
//        Long id = (Long) cmd.getBizField();
//        return BaseCacheResult.success(getComponentName(), stockHoldInfoService.getById(id));
//    }
//
//    @Override
//    public <K, F> CacheResult batchLoad(HashCacheBatchLoadCommand<K, F> cmd) {
//        List<Long> ids = cmd.getBizFields().stream()
//                .map(item -> (Long) item)
//                .collect(Collectors.toList());
//        List<StockHoldInfo> stockHoldInfos = stockHoldInfoService.listByIds(ids);
//        if (CollUtil.isNotEmpty(stockHoldInfos)) {
//            Map<Long, StockHoldInfo> stockHoldInfoMap = stockHoldInfos.stream()
//                    .collect(Collectors.toMap(StockHoldInfo::getId, stockHoldInfo -> stockHoldInfo));
//            return BaseCacheResult.success(getComponentName(), stockHoldInfoMap);
//        }
//        return BaseCacheResult.success(getComponentName(), Collections.emptyMap());
//    }
//}
