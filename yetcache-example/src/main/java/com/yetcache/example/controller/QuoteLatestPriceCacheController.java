package com.yetcache.example.controller;

import com.yetcache.core.result.BaseCacheResult;
import com.yetcache.example.quote.service.QuoteLatestPriceCacheService;
import com.yetcache.example.quote.vo.QuoteLatestPriceVO;
import com.yetcache.example.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author walter.yan
 * @since 2025/8/31
 */
@RestController
@RequestMapping("/api/quote/latest-price")
@Slf4j
public class QuoteLatestPriceCacheController {
    @Autowired
    private QuoteLatestPriceCacheService cacheService;

    @PostMapping("/get")
    public R<QuoteLatestPriceVO> get(@RequestBody QuoteLatestPriceQueryDTO dto) {
        BaseCacheResult<QuoteLatestPriceVO> cacheResult = cacheService.get(dto.getQuery(), dto.getContext());
        if (cacheResult.isSuccess()) {
            return R.ok(cacheResult.getValue());
        }
        log.debug("[Yetcache]QuoteLatestPriceCacheController get quote latest price cache, " +
                "cacheService return fail code, query dto: {}, cacheResult: {}", dto, cacheResult);
        return R.fail("操作失败！");
    }
}
