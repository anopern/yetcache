//package com.yetcache.agent.governance.plugin;
//
//import com.yetcache.agent.core.CacheAgentMethod;
//import com.yetcache.agent.interceptor.CacheAccessKey;
//import com.yetcache.agent.interceptor.InvocationChain;
//import com.yetcache.agent.interceptor.DefaultInvocationContext;
//import com.yetcache.agent.interceptor.InvocationInterceptor;
//import com.yetcache.agent.protect.PenetrationProtector;
//import com.yetcache.core.result.Result;
//import com.yetcache.core.result.ResultFactory;
//
///**
// * @author walter.yan
// * @since 2025/7/16
// */
//public class PenetrationProtectInterceptor implements InvocationInterceptor {
//    protected final PenetrationProtector protector;
//    protected final boolean allowNullValue;
//
//    public PenetrationProtectInterceptor(PenetrationProtector protector, boolean allowNullValue) {
//        this.protector = protector;
//        this.allowNullValue = allowNullValue;
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public <R extends Result<?>> R intercept(DefaultInvocationContext ctx, InvocationChain<R> chain) throws Throwable {
//        // 获取 method 枚举（提前 parse）
//        CacheAgentMethod method = CacheAgentMethod.from(ctx.getMethodName()).orElse(null);
//        if (method == null) {
//            return chain.invoke(ctx); // fallback
//        }
//
//        // 构建访问 key（必须事先设置）
//        CacheAccessKey accessKey = ctx.getAccessKey();
//        if (null == accessKey) {
//            return chain.invoke(ctx); // fallback
//        }
//
//        // 判断是否已被标记为 null，直接短路返回
//        if (protector.isMarkedAsNull(accessKey)) {
//            return (R) ResultFactory.notFound(ctx.getComponentNane());
//        }
//
//        // 正常执行链条
//        R result = chain.invoke(ctx);
//
//        // 命中但返回为 null，则可认为穿透，进行标记
//        if (result.isSuccess() && result.value() == null && allowNullValue) {
//            protector.markAsNull(accessKey);
//        }
//
//        return result;
//    }
//
//    @Override
//    public boolean supports(String methodName) {
//        return CacheAgentMethod.from(methodName)
//                .map(m -> m == CacheAgentMethod.GET || m == CacheAgentMethod.LIST_ALL)
//                .orElse(false);
//    }
//}
