package com.yetcache.example.cache.service;

import cn.hutool.core.collection.CollUtil;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.result.CacheResultUtils;
import com.yetcache.core.result.HitTier;
import com.yetcache.core.result.SingleCacheResult;
import com.yetcache.example.cache.agent.StockHoldInfoCacheAgent;
import com.yetcache.example.entity.StockHoldInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
@Component
public final class StockHoldInfoCacheService {
    private final StockHoldInfoCacheAgent stockHoldInfoCacheAgent;

    @Autowired
    public StockHoldInfoCacheService(StockHoldInfoCacheAgent stockHoldInfoCacheAgent) {
        this.stockHoldInfoCacheAgent = stockHoldInfoCacheAgent;
    }

    public Optional<StockHoldInfo> get(String fundAccount, Long id) {
        SingleCacheResult<CacheValueHolder<StockHoldInfo>> result = CacheResultUtils.getTypedResult(
                stockHoldInfoCacheAgent.get(fundAccount, id));
        if (result.isSuccess() && HitTier.NONE != result.hitTierInfo().hitTier()) {
            CacheValueHolder<StockHoldInfo> valueHolder = result.value();
            return Optional.of(valueHolder.getValue());
        }
        return Optional.empty();
    }

    public List<StockHoldInfo> batchGet(String fundAccount, List<Long> ids) {
        SingleCacheResult<Map<Object, CacheValueHolder<StockHoldInfo>>> result = CacheResultUtils.getTypedResult(
                stockHoldInfoCacheAgent.batchGet(fundAccount, Arrays.asList(ids.toArray())));
        if (result.isSuccess()) {
            Map<Object, CacheValueHolder<StockHoldInfo>> valueHolderMap = result.value();
            if (CollUtil.isNotEmpty(valueHolderMap)) {
                return valueHolderMap.values().stream()
                        .map(CacheValueHolder::getValue)
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
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
