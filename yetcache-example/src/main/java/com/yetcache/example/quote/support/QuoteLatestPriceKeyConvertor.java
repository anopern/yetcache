package com.yetcache.example.quote.support;

import com.yetcache.core.support.key.AbstractKeyConverter;
import com.yetcache.example.quote.key.QuoteLatestPriceCacheKey;

/**
 * @author walter.yan
 * @since 2025/8/28
 */
public class QuoteLatestPriceKeyConvertor extends AbstractKeyConverter<QuoteLatestPriceCacheKey> {
    private final String keyFmt;

    public QuoteLatestPriceKeyConvertor(String keyPrefix, boolean useHashTag) {
        super(keyPrefix, useHashTag);
        if (useHashTag) {
            keyFmt = keyPrefix + ":%s:lv%s:ses%s:{%s}";
        } else {
            keyFmt = keyPrefix + ":%s:lv%s:ses%s:%s";
        }
    }

    @Override
    public String convert(QuoteLatestPriceCacheKey bizKey) {
        return String.format(keyFmt, bizKey.getMarket(), bizKey.getLevel(), bizKey.getSession(), bizKey.getSymbol());
    }

    @Override
    public QuoteLatestPriceCacheKey revert(String key) {
        if (key == null) {
            return null;
        }
        String txt = key.replace(keyPrefix + ":", "");
        String[] parts = txt.split(":");
        String market = parts[0];
        String level = parts[1];
        String session = parts[2];
        String symbol = parts[3];
        return new QuoteLatestPriceCacheKey(market, symbol, Integer.parseInt(level), Integer.parseInt(session));
    }
}
