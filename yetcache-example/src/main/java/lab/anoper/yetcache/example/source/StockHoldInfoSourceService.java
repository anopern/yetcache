package lab.anoper.yetcache.example.source;

import lab.anoper.yetcache.example.assember.StockHoldInfoConverter;
import lab.anoper.yetcache.example.base.common.cache.dto.StockHoldInfoDTO;
import lab.anoper.yetcache.example.domain.entity.StockHoldInfo;
import lab.anoper.yetcache.example.service.IStockHoldInfoService;
import lab.anoper.yetcache.source.impl.AbstractHashCacheSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("stockHoldInfoSourceService")
public class StockHoldInfoSourceService extends AbstractHashCacheSourceService<StockHoldInfoDTO> {
    @Autowired
    private IStockHoldInfoService stockHoldInfoService;

    @Override
    public List<StockHoldInfoDTO> queryList(Long tenantId, String bizKey) {
        List<StockHoldInfo> stockHoldInfos = stockHoldInfoService.listByFundAccount(bizKey);
        return StockHoldInfoConverter.INSTANCE.doListToDTOList(stockHoldInfos);
    }
}
