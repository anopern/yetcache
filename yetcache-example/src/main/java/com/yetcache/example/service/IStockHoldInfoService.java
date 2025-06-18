package com.yetcache.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yetcache.example.domain.entity.StockHoldInfo;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface IStockHoldInfoService extends IService<StockHoldInfo> {
    List<StockHoldInfo> listByFundAccount(@NotNull String fundAccount);
}
