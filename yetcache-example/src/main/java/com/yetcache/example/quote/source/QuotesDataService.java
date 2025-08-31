package com.yetcache.example.quote.source;

import com.yetcache.example.domain.entity.QuoteSimpleQuoteList;
import com.yetcache.example.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author walter.yan
 * @since 2025/8/28
 */
@FeignClient(name = "quotes-dataservice-app",
        url = "https://hz-sit.yxzq.com")
public interface QuotesDataService {

    @PostMapping(value = "/quotes-dataservice-app/api/v3/simple-quote",
            consumes = {"application/json"}, produces = {"application/json"})
    R<QuoteSimpleQuoteList> querySimpleQuote(@RequestBody QuoteSimpleQuoteReqDTO req);
}
