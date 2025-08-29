package com.yetcache.example.quote.interceptor;

import com.yetcache.agent.core.BehaviorType;
import com.yetcache.agent.core.StructureBehaviorKey;
import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.structure.kv.KvCacheAgentScope;
import com.yetcache.agent.core.structure.kv.get.KvCacheAgentGetInvocationCommand;
import com.yetcache.agent.interceptor.CacheInterceptor;
import com.yetcache.agent.interceptor.CacheInvocationContext;
import com.yetcache.agent.interceptor.ChainRunner;
import com.yetcache.core.result.BaseCacheResult;
import com.yetcache.core.result.CacheResult;
import com.yetcache.example.quote.vo.QuoteLatestPriceVO;

/**
 * @author walter.yan
 * @since 2025/8/29
 */
public class QuoteLatestPriceCanonicalKeyInterceptor implements CacheInterceptor {
    @Override
    public String id() {
        return "QuoteLatestPriceCanonicalKeyInterceptor";
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public boolean supports(StructureBehaviorKey sbKey) {
        return sbKey.getStructureType() == StructureType.KV
                && sbKey.getBehaviorType() == BehaviorType.GET;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CacheResult invoke(CacheInvocationContext ctx, ChainRunner runner) throws Throwable {
        KvCacheAgentGetInvocationCommand cmd = (KvCacheAgentGetInvocationCommand) ctx.getCommand();
        Object bizKey = cmd.getBizKey();
        KvCacheAgentScope agentScope = (KvCacheAgentScope) ctx.getAgentScope();
        String cacheAgentName = agentScope.getCacheAgentName();
        BaseCacheResult<QuoteLatestPriceVO> result = (BaseCacheResult<QuoteLatestPriceVO>) runner.proceed(ctx);
        if (result.isSuccess() && result.value() != null) {
            QuoteLatestPriceVO vo = result.value();

        }
        return null;
    }
}
