package com.yetcache.example.quote.service;

import com.yetcache.agent.agent.kv.BaseKvCacheAgent;
import com.yetcache.core.support.CacheValueHolder;
import com.yetcache.core.result.BaseCacheResult;
import com.yetcache.example.quote.QuoteLatestPriceQuery;
import com.yetcache.example.quote.QuoteLatestPriceQueryContext;
import com.yetcache.example.quote.YxExchangeTypeEnum;
import com.yetcache.example.quote.key.QuoteLatestPriceCacheKey;
import com.yetcache.example.quote.vo.QuoteLatestPriceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author walter.yan
 * @since 2025/8/28
 */
@Component
@Slf4j
public class QuoteLatestPriceCacheServiceImpl implements QuoteLatestPriceCacheService {
    @Qualifier("quoteLatestPriceCacheAgent")
    @Autowired
    private BaseKvCacheAgent cacheAgent;

    @Override
    public BaseCacheResult<QuoteLatestPriceVO> get(QuoteLatestPriceQuery query, QuoteLatestPriceQueryContext context) {
        YxExchangeTypeEnum exchangeTypeEnum = YxExchangeTypeEnum.getByValue(query.getExchangeType());
        QuoteLatestPriceCacheKey bizKey = QuoteLatestPriceCacheKey.builder()
                .market(exchangeTypeEnum.getMarket())
                .symbol(query.getCode())
                .level(context.getLevel())
                .session(context.getSession())
                .build();
        BaseCacheResult<CacheValueHolder<QuoteLatestPriceVO>> result = cacheAgent.get(bizKey);
        if (result.isSuccess() && result.value() != null) {
            CacheValueHolder<QuoteLatestPriceVO> valueHolder = result.value();
            return BaseCacheResult.success(result.getComponentName(), valueHolder.getValue());
        }
        return BaseCacheResult.fail(result.getComponentName(), result.errorInfo());
    }
}
