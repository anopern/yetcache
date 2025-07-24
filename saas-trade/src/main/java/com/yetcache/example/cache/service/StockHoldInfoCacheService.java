package com.yetcache.example.cache.service;

import com.yetcache.agent.result.DynamicHashCacheAgentResult;
import com.yetcache.core.context.CacheAccessContext;
import com.yetcache.example.cache.agent.StockHoldInfoCacheAgent;
import com.yetcache.example.entity.StockHoldInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public StockHoldInfo get(String fundAccount, Long id) {
        DynamicHashCacheAgentResult<String, Long, StockHoldInfo> result = stockHoldInfoCacheAgent.get(fundAccount, id);
        return result.getFirstValueOrNull();
    }

    public List<StockHoldInfo> listAll(String fundAccount) {
        return listAll(fundAccount, false);
    }

    public List<StockHoldInfo> listAll(String fundAccount, boolean forceRefresh) {
        CacheAccessContext.setForceRefresh(forceRefresh);
        DynamicHashCacheAgentResult<String, Long, StockHoldInfo> result = stockHoldInfoCacheAgent.listAll(fundAccount);
        return result.toValueList();
    }

    public boolean refreshAll(String fundAccount) {
        DynamicHashCacheAgentResult<String, Long, StockHoldInfo> result = stockHoldInfoCacheAgent.refreshAll(fundAccount);
        return result.isSuccess();
    }
}
