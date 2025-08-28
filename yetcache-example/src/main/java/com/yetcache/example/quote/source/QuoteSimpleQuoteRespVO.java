package com.yetcache.example.quote.source;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 简单报价接口的返回值
 *
 * @author walter.yan
 * @since 2025/4/15
 */
@Data
public class QuoteSimpleQuoteRespVO  {
    private Id id;
    private Integer level;
    private BigDecimal priceBase;
    private QuoteData quoteData = new QuoteData();

    @Data
    public static class Id {
        private String market;
        private String symbol;
        private Integer greyMarket;
    }

    @Data
    public static class QuoteData {
        private Long seq;
        private Long latestTime;
        private BigDecimal latestPrice;
        private BigDecimal netchng;
        private BigDecimal pctchng;
        private Integer mktStatus;
        private Integer trdStatus;
        private Integer qtType;
        private BigDecimal high;
        private BigDecimal low;
        private BigDecimal volume;
        private BigDecimal amount;
        private BigDecimal preClose;

        private BigDecimal latestPriceReal;
        private BigDecimal preCloseReal;
    }
}
