//package com.yetcache.agent.interceptor.v2;
//
//import com.yetcache.agent.core.AgentScope;
//import com.yetcache.agent.core.structure.dynamichash.DynamicHashAgentScope;
//import com.yetcache.agent.interceptor.v2.CacheInvocationCommandV2;
//import com.yetcache.core.result.CacheResult;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
///**
// * @author walter.yan
// * @since 2025/7/29
// */
//@AllArgsConstructor
//@NoArgsConstructor
//@Data
//public final class CacheInvocationContextV2 {
//    private CacheInvocationCommandV2 command;
//    private CacheResult result;
//
//    private AgentScope agentScope;
//
//    private boolean interrupted;
//    private String interruptReason;
//
//    public void interrupt(String reason) {
//        interrupted = true;
//        interruptReason = reason;
//    }
//
//    @SuppressWarnings("unchecked")
//    public <K, F, V> DynamicHashAgentScope<K, F, V> asDynamicHashAgentScope() {
//        return (DynamicHashAgentScope<K, F, V>) agentScope;
//    }
//}
