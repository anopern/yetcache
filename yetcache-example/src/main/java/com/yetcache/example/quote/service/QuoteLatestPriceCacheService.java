package com.yetcache.example.quote.service;

import com.yetcache.core.result.BaseCacheResult;
import com.yetcache.example.quote.QuoteLatestPriceQuery;
import com.yetcache.example.quote.QuoteLatestPriceQueryContext;
import com.yetcache.example.quote.vo.QuoteLatestPriceVO;

/**
 * @author walter.yan
 * @since 2025/8/28
 */
public interface QuoteLatestPriceCacheService {
    BaseCacheResult<QuoteLatestPriceVO> get(QuoteLatestPriceQuery query, QuoteLatestPriceQueryContext context);
}
