package com.yetcache.example.cache.service;

import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.result.CacheResultUtils;
import com.yetcache.core.result.HitTier;
import com.yetcache.core.result.SingleCacheResult;
import com.yetcache.example.cache.agent.StockHoldInfoCacheAgent;
import com.yetcache.example.entity.StockHoldInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
@Component
public final class StockHoldInfoCacheService {
    private final StockHoldInfoCacheAgent<StockHoldInfo> stockHoldInfoCacheAgent;

    @Autowired
    public StockHoldInfoCacheService(StockHoldInfoCacheAgent<StockHoldInfo> stockHoldInfoCacheAgent) {
        this.stockHoldInfoCacheAgent = stockHoldInfoCacheAgent;
    }

    public Optional<StockHoldInfo> get(String fundAccount, Long id) {
        SingleCacheResult<CacheValueHolder<StockHoldInfo>> result = CacheResultUtils.getTypedValue(stockHoldInfoCacheAgent.get(fundAccount, id));
        if (result.isSuccess() && HitTier.NONE != result.hitTierInfo().hitTier()) {
            CacheValueHolder<StockHoldInfo> valueHolder = result.value();
            return Optional.of(valueHolder.getValue());
        }
        return Optional.empty();
    }

//    public List<StockHoldInfo> listAll(String fundAccount) {
//        return listAll(fundAccount, false);
//    }
//
//    public List<StockHoldInfo> listAll(String fundAccount, boolean forceRefresh) {
//        CacheAccessContext.setForceRefresh(forceRefresh);
//        DynamicHashCacheAgentResult<String, Long, StockHoldInfo> result = stockHoldInfoCacheAgent.listAll(fundAccount);
//        return result.toValueList();
//    }
//
//    public boolean refreshAll(String fundAccount) {
//        DynamicHashCacheAgentResult<String, Long, StockHoldInfo> result = stockHoldInfoCacheAgent.refreshAll(fundAccount);
//        return result.isSuccess();
//    }
}
