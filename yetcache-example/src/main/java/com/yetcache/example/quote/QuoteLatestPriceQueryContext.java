package com.yetcache.example.quote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuoteLatestPriceQueryContext {
    private Integer level;
    private Integer session;
}
