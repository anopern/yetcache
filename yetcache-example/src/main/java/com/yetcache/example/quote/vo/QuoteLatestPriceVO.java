package com.yetcache.example.quote.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author walter.yan
 * @since 2025/8/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuoteLatestPriceVO {
    private Integer exchangeType;
    private String code;
    private String market;
    private String symbol;

    private BigDecimal preClose;
    private BigDecimal latestPrice;
}
