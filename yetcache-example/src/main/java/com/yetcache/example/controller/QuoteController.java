package com.yetcache.example.controller;

import com.yetcache.core.result.BaseCacheResult;
import com.yetcache.example.quote.QuoteLatestPriceQuery;
import com.yetcache.example.quote.QuoteLatestPriceQueryContext;
import com.yetcache.example.quote.service.QuoteLatestPriceCacheService;
import com.yetcache.example.quote.vo.QuoteLatestPriceVO;
import com.yetcache.example.result.R;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author walter.yan
 * @since 2025/8/28
 */
@RestController
@RequestMapping("/api/quote")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class QuoteController {
    @Data
    public static class QuoteLatestPriceDTO {
        private QuoteLatestPriceQuery query;
        private QuoteLatestPriceQueryContext context;
    }

    private final QuoteLatestPriceCacheService quoteLatestPriceCacheService;

    @PostMapping("/latestPrice")
    public R<QuoteLatestPriceVO> latestPrice(@RequestBody QuoteLatestPriceDTO dto) {
        BaseCacheResult<QuoteLatestPriceVO> ret = quoteLatestPriceCacheService.get(dto.getQuery(), dto.getContext());
        if (ret.isSuccess()) {
            return R.ok(ret.getValue());
        }
        return R.fail("操作失败！");
    }
}
