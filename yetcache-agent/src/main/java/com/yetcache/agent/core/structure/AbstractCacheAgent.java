//package com.yetcache.agent.core.structure;
//
//import com.yetcache.agent.interceptor.*;
//import com.yetcache.core.result.Result;
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.function.Supplier;
//
///**
// * @author walter.yan
// * @since 2025/7/15
// */
//public abstract class AbstractCacheAgent {
//    protected final String componentName;
//    protected final List<InvocationInterceptor> interceptors = new CopyOnWriteArrayList<>();
////
//    protected AbstractCacheAgent(String componentName) {
//        this.componentName = componentName;
//    }
//
//    protected <R extends Result<?>> R invoke(String method, Supplier<R> business) {
//        return invoke(method, business, null);
//    }
//
//    /**
//     * 子类提供默认失败返回结果（结构相关）
//     */
//    protected abstract <R extends Result<?>> R defaultFail(String method, Throwable t);
//}
