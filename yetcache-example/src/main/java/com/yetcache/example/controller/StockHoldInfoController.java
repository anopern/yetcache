package com.yetcache.example.controller;

import com.yetcache.example.cache.service.StockHoldInfoCacheService;
import com.yetcache.example.entity.StockHoldInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@RestController
@RequestMapping("/api/stock-hold-infos")
@Slf4j
public class StockHoldInfoController {
    private final StockHoldInfoCacheService cacheService;

    @Autowired
    public StockHoldInfoController(StockHoldInfoCacheService cacheService) {
        this.cacheService = cacheService;
    }

    @PostMapping("/get")
    public StockHoldInfo get(@RequestBody StockHoldInfo dto) {
        Optional<StockHoldInfo> optional = cacheService.get(dto.getFundAccount(), dto.getId());
        return optional.orElse(null);
    }

    @PostMapping("/batchGet")
    public List<StockHoldInfo> batchGet(@RequestBody StockHoldInfoBatchGetReq req) {
        return cacheService.batchGet(req.getFundAccount(), req.getIds());
    }

    @PostMapping("/remove")
    public void remove(@RequestBody StockHoldInfoBatchGetReq req) {
          cacheService.remove(req.getFundAccount(), req.getId());
    }
//
//    @PostMapping("/listAll")
//    public List<StockHoldInfo> listAll(@RequestBody StockHoldInfo dto) {
//        boolean forceRefresh = dto.getForceRefresh() != null && dto.getForceRefresh();
//        return cacheService.listAll(dto.getFundAccount(), forceRefresh);
//    }
//
//    @PostMapping("/refreshAll")
//    public Boolean refreshAll(@RequestBody StockHoldInfo dto) {
//        return cacheService.refreshAll(dto.getFundAccount());
//    }
}
