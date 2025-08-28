package com.yetcache.example.quote.key;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuoteLatestPriceCacheKey {
    private String market;
    private String symbol;
    private Integer level;
    private Integer session;
}
