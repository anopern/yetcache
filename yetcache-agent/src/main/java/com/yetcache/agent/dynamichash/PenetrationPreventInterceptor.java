package com.yetcache.agent.dynamichash;

import com.yetcache.agent.interceptor.CacheInvocationChain;
import com.yetcache.agent.interceptor.CacheInvocationContext;
import com.yetcache.agent.interceptor.CacheInvocationInterceptor;
import com.yetcache.core.config.PenetrationProtectConfig;
import com.yetcache.core.protect.PenetrationProtectCache;
import com.yetcache.core.result.CacheAccessResult;
import com.yetcache.core.result.CacheOutcome;

/**
 * @author walter.yan
 * @since 2025/7/15
 */
public class PenetrationPreventInterceptor implements CacheInvocationInterceptor {
    protected PenetrationProtectCache localProtectCache;
    protected PenetrationProtectCache remoteProtectCache;

    public PenetrationPreventInterceptor(PenetrationProtectCache localProtectCache,
                                         PenetrationProtectCache remoteProtectCache) {
        this.localProtectCache = localProtectCache;
        this.remoteProtectCache = remoteProtectCache;
    }

    @Override
    public <R extends CacheAccessResult<?>> R intercept(CacheInvocationContext ctx, CacheInvocationChain<R> chain) throws Throwable {
//        // 判断是否需要阻断，例如 key+field 组合已被 block 标记
//        if (blocker.shouldBlock(ctx.method(), ctx.bizKey(), ctx.bizField())) {
//            return (R) CacheAgentResult.blocked(ctx.component());  // 返回阻断结果，结构清晰
//        }
//
//        // 放行执行原逻辑
//        R result = next.get();
//
//        // 若本次是穿透，并失败，则标记
//        if (result.outcome() == CacheOutcome.FAIL || result.outcome() == CacheOutcome.NOT_FOUND) {
//            blocker.markBlock(ctx.method(), ctx.bizKey(), ctx.bizField());
//        }

//        return result;
        return null;
    }
}
