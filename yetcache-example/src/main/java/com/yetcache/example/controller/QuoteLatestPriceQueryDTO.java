package com.yetcache.example.controller;

import com.yetcache.example.quote.QuoteLatestPriceQuery;
import com.yetcache.example.quote.QuoteLatestPriceQueryContext;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/8/31
 */
@Data
public class QuoteLatestPriceQueryDTO {
    private QuoteLatestPriceQuery query;
    private QuoteLatestPriceQueryContext context;
}
