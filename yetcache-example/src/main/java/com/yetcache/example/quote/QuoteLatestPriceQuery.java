package com.yetcache.example.quote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/28
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class QuoteLatestPriceQuery {
    private Integer exchangeType;
    private String code;
}
