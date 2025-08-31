package com.yetcache.agent.agent.kv.interceptor;

import com.yetcache.agent.agent.BehaviorType;
import com.yetcache.agent.agent.StructureBehaviorKey;
import com.yetcache.agent.agent.StructureType;
import com.yetcache.agent.agent.kv.KvCacheAgentScope;
import com.yetcache.agent.interceptor.CacheInterceptor;
import com.yetcache.agent.interceptor.CacheInvocationContext;
import com.yetcache.agent.interceptor.ChainRunner;
import com.yetcache.agent.interceptor.InterceptorSupportCriteria;
import com.yetcache.core.result.CacheResult;
import com.yetcache.core.result.HitLevel;
import com.yetcache.core.support.CacheValueHolder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Slf4j
public class KvCachePutInterceptor implements CacheInterceptor {
    @Override
    public String id() {
        return "kv-cache-put-interceptor";
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public boolean supports(InterceptorSupportCriteria criteria) {
        StructureBehaviorKey sbKey = criteria.getSbKey();
        return StructureType.KV.equals(sbKey.getStructureType())
                && BehaviorType.GET.equals(sbKey.getBehaviorType());
    }

    @Override
    public CacheResult invoke(CacheInvocationContext ctx, ChainRunner runner) throws Throwable {
        KvCacheAgentGetInvocationCommand cmd = (KvCacheAgentGetInvocationCommand) ctx.getCommand();
        Object bizKey = cmd.getBizKey();
        KvCacheAgentScope agentScope = (KvCacheAgentScope) ctx.getAgentScope();

        CacheResult result = runner.proceed(ctx);
        if (!result.isSuccess()) {
            return result;
        }
        // 如果是命中级别是原数据，则需要将数据重写回到缓存中
        if (result.hitLevelInfo().hitLevel() == HitLevel.SOURCE) {
            CacheValueHolder<?> valueHolder = (CacheValueHolder<?>) result.value();
            agentScope.getCachePutPort().put(bizKey, valueHolder.getValue());
        }
        return result;
    }
}
