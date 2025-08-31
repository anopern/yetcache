package com.yetcache.example.quote.interceptor;

import com.yetcache.agent.agent.BehaviorType;
import com.yetcache.agent.agent.ChainKey;
import com.yetcache.agent.agent.StructureType;
import com.yetcache.agent.interceptor.CacheInterceptor;
import com.yetcache.agent.interceptor.CacheInvocationContext;
import com.yetcache.agent.interceptor.ChainRunner;
import com.yetcache.core.result.CacheResult;
import com.yetcache.example.config.CacheAgentNames;

/**
 * @author walter.yan
 * @since 2025/8/31
 */
public class QuoteLatestPriceSourceErrorFallbackInterceptor implements CacheInterceptor {
    @Override
    public String id() {
        return "QuoteLatestPriceSourceErrorFallbackInterceptor";
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 1;
    }

    @Override
    public boolean supports(ChainKey chainKey) {
        return StructureType.KV == chainKey.getStructureType()
                && BehaviorType.GET == chainKey.getBehaviorType()
                && CacheAgentNames.QUOTE_LATEST_PRICE_CACHE_AGENT.equals(chainKey.getCacheAgentName());
    }

    @Override
    public CacheResult invoke(CacheInvocationContext context, ChainRunner runner) throws Throwable {
        return null;
    }
}
