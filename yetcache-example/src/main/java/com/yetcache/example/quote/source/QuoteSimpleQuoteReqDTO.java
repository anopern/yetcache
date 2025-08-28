package com.yetcache.example.quote.source;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 简单报价接口的返回值
 *
 * @author walter.yan
 * @since 2025/4/15
 */
@Data
@Builder
public class QuoteSimpleQuoteReqDTO {
    private List<Id> ids;
    private List<String> props;
    private Integer level;
    private Integer session;

    @Data
    @Builder
    public static class Id {
        private String market;
        private String symbol;
        private Integer greyMarket;
    }
}
