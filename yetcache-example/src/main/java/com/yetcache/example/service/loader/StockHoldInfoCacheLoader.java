package com.yetcache.example.service.loader;

import com.yetcache.agent.core.structure.dynamichash.AbstractDynamicHashCacheLoader;
import com.yetcache.agent.core.structure.dynamichash.HashCacheBatchLoadCommand;
import com.yetcache.agent.core.structure.dynamichash.HashCacheSingleLoadCommand;
import com.yetcache.core.result.CacheResult;
import com.yetcache.core.result.SingleCacheResult;
import com.yetcache.example.entity.StockHoldInfo;
import com.yetcache.example.service.IStockHoldInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author walter.yan
 * @since 2025/7/2
 */
@Component
public class StockHoldInfoCacheLoader extends AbstractDynamicHashCacheLoader {
    @Autowired
    private IStockHoldInfoService stockHoldInfoService;

    @Override
    public CacheResult load(HashCacheSingleLoadCommand cmd) {
        Long id = (Long) cmd.getBizField();
        return SingleCacheResult.success(stockHoldInfoService.getById(id));
    }

    @Override
    public CacheResult batchLoad(HashCacheBatchLoadCommand cmd) {
        List<Long> ids = cmd.getBizFields().stream()
                .map(item -> (Long) item)
                .collect(Collectors.toList());
        List<StockHoldInfo> stockHoldInfos = stockHoldInfoService.listByIds(ids);
        return SingleCacheResult.success(stockHoldInfos);
    }

    //    @Override
//    public Map<Long, StockHoldInfo> loadAll(String fundAccount) {
//        return stockHoldInfoService.listByFundAccount(fundAccount)
//                .stream().collect(Collectors.toMap(StockHoldInfo::getId, stockHoldInfo -> stockHoldInfo));
//    }
}
