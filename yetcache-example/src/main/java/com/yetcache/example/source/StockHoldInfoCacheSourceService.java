package com.yetcache.example.source;

import com.yetcache.example.assember.StockHoldInfoConverter;
import com.yetcache.example.base.common.cache.dto.StockHoldInfoDTO;
import com.yetcache.example.domain.entity.StockHoldInfo;
import com.yetcache.example.service.IStockHoldInfoService;
import com.yetcache.source.impl.AbstractMultiHashCacheSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("stockHoldInfoSourceService")
public class StockHoldInfoCacheSourceService extends AbstractMultiHashCacheSourceService<StockHoldInfoDTO> {
    @Autowired
    private IStockHoldInfoService stockHoldInfoService;

    @Override
    public List<StockHoldInfoDTO> queryList(Long tenantId, String bizKey) {
        List<StockHoldInfo> stockHoldInfos = stockHoldInfoService.listByFundAccount(bizKey);
        return StockHoldInfoConverter.INSTANCE.doListToDTOList(stockHoldInfos);
    }
}
