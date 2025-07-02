package com.yetcache.example.service.loader;

import com.yetcache.core.cache.loader.AbstractDynamicHashCacheLoader;
import com.yetcache.example.entity.StockHoldInfo;
import com.yetcache.example.service.IStockHoldInfoService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author walter.yan
 * @since 2025/7/2
 */
@Component
public class StockHoldInfoCacheLoader extends AbstractDynamicHashCacheLoader<String, Long, StockHoldInfo> {
    @Autowired
    private IStockHoldInfoService stockHoldInfoService;

    @Override
    public StockHoldInfo load(String s, Long bizField) {
        return stockHoldInfoService.getById(bizField);
    }
}
