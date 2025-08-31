package com.yetcache.example.quote.mocker;

import com.yetcache.example.quote.key.QuoteLatestPriceCacheKey;
import com.yetcache.example.quote.vo.QuoteLatestPriceVO;

import java.math.BigDecimal;
import java.util.Random;

/**
 * @author walter.yan
 * @since 2025/8/31
 */
public class QuoteLatestPriceMocker {
    private static final Random RANDOM = new Random();

    public static QuoteLatestPriceVO mock(QuoteLatestPriceCacheKey key) {
        int mockLevel = key.getLevel() != null && key.getLevel() == 1000
                ? RANDOM.nextInt(9) + 1
                : key.getLevel();

        return QuoteLatestPriceVO.builder()
                .market(key.getMarket())
                .symbol(key.getSymbol())
                .preClose(BigDecimal.valueOf(RANDOM.nextDouble() * 100))
                .latestPrice(BigDecimal.valueOf(RANDOM.nextDouble() * 100))
                .level(mockLevel)
                .build();
    }
}
