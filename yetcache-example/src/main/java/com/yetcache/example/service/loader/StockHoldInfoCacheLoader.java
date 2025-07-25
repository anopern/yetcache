package com.yetcache.example.service.loader;

import com.yetcache.agent.core.structure.dynamichash.AbstractDynamicHashCacheLoader;
import com.yetcache.example.entity.StockHoldInfo;
import com.yetcache.example.service.IStockHoldInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author walter.yan
 * @since 2025/7/2
 */
@Component
public class StockHoldInfoCacheLoader extends AbstractDynamicHashCacheLoader<String, Long, StockHoldInfo> {
    @Autowired
    private IStockHoldInfoService stockHoldInfoService;

    @Override
    public StockHoldInfo load(String fundAccount, Long id) {
        return stockHoldInfoService.getById(id);
    }

    @Override
    public Map<Long, StockHoldInfo> loadAll(String fundAccount) {
        return stockHoldInfoService.listByFundAccount(fundAccount)
                .stream().collect(Collectors.toMap(StockHoldInfo::getId, stockHoldInfo -> stockHoldInfo));
    }
}
