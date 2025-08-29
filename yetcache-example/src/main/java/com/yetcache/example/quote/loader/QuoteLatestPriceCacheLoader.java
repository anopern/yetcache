package com.yetcache.example.quote.loader;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.yetcache.agent.agent.kv.loader.AbstractKvCacheLoader;
import com.yetcache.agent.agent.kv.loader.KvCacheBatchLoadCommand;
import com.yetcache.agent.agent.kv.loader.KvCacheLoadCommand;
import com.yetcache.core.result.BaseCacheResult;
import com.yetcache.core.result.CacheResult;
import com.yetcache.core.result.ErrorInfo;
import com.yetcache.example.entity.QuoteSimpleQuoteList;
import com.yetcache.example.entity.QuoteSimpleQuoteRespVO;
import com.yetcache.example.quote.key.QuoteLatestPriceCacheKey;
import com.yetcache.example.quote.source.QuoteSimpleQuoteReqDTO;
import com.yetcache.example.quote.source.QuotesDataService;
import com.yetcache.example.quote.vo.QuoteLatestPriceVO;
import com.yetcache.example.result.R;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author walter.yan
 * @since 2025/8/28
 */
@AllArgsConstructor
@Slf4j
public class QuoteLatestPriceCacheLoader extends AbstractKvCacheLoader<QuoteLatestPriceCacheKey> {
    private final QuotesDataService quotesDataService;

    @Override
    public String getLoaderName() {
        return null;
    }

    @Override
    public CacheResult load(KvCacheLoadCommand<QuoteLatestPriceCacheKey> cmd) {
        QuoteLatestPriceCacheKey bizKey = cmd.getBizKey();
        QuoteSimpleQuoteReqDTO.Id id = QuoteSimpleQuoteReqDTO.Id.builder()
                .market(bizKey.getMarket())
                .symbol(bizKey.getSymbol())
                .greyMarket(2)
                .build();
        QuoteSimpleQuoteReqDTO req = QuoteSimpleQuoteReqDTO.builder()
                .ids(Lists.newArrayList(id))
                .level(cmd.getBizKey().getLevel())
                .session(cmd.getBizKey().getSession())
                .build();

        try {
            R<QuoteSimpleQuoteList> resp = quotesDataService.querySimpleQuote(req);
            if (resp.getCode() == 0) {
                QuoteSimpleQuoteList respVOList = resp.getData();
                if (null == respVOList || CollUtil.isEmpty(respVOList.getList())) {
                    return BaseCacheResult.success(getLoaderName(), null);
                }
                QuoteSimpleQuoteRespVO respVO = respVOList.getList().get(0);
                QuoteLatestPriceVO priceVO = QuoteLatestPriceVO.builder()
                        .exchangeType(respVO.getId().getGreyMarket())
                        .code(respVO.getId().getSymbol())
                        .market(respVO.getId().getMarket())
                        .symbol(respVO.getId().getSymbol())
                        .preClose(respVO.getQuoteData().getPreClose())
                        .latestPrice(respVO.getQuoteData().getLatestPrice())
                        .build();
                return BaseCacheResult.success(getLoaderName(), priceVO);
            } else {
                log.error("请求行情查询最新价异常，请求参数：{}，服务器回复：{}", req, resp);
                return BaseCacheResult.fail(getLoaderName(), ErrorInfo.of(null));
            }
        } catch (Exception e) {
            log.error("请求行情查询最新价异常，请求参数：{}", req, e);
            return BaseCacheResult.fail(getLoaderName(), ErrorInfo.of(e));
        }
    }

    @Override
    public CacheResult batchLoad(KvCacheBatchLoadCommand<QuoteLatestPriceCacheKey> cmd) {
        return null;
    }
}
