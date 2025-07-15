package com.yetcache.example.controller;

import com.yetcache.example.cache.service.StockHoldInfoCacheService;
import com.yetcache.example.entity.StockHoldInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@RestController
@RequestMapping("/api/stock-hold-infos")
@Slf4j
@AllArgsConstructor
public class StockHoldInfoController {
    private final StockHoldInfoCacheService cacheService;

    @PostMapping("/get")
    public StockHoldInfo get(@RequestBody StockHoldInfo dto) {
        return cacheService.get(dto.getFundAccount(), dto.getId());
    }

    @PostMapping("/listAll")
    public List<StockHoldInfo> listAll(@RequestBody StockHoldInfo dto) {
        return cacheService.listAll(dto.getFundAccount(), dto.getForceRefresh());
    }

    @PostMapping("/refreshAll")
    public Boolean refreshAll(@RequestBody StockHoldInfo dto) {
        return cacheService.refreshAll(dto.getFundAccount());
    }
}
