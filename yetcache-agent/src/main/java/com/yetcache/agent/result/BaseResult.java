//package com.yetcache.agent.result;
//
//import com.yetcache.core.result.Result;
//import com.yetcache.core.result.CacheOutcome;
//import lombok.Getter;
//
//import java.io.Serializable;
//import java.util.Objects;
//
///**
// * @author walter.yan
// * @since 2025/7/13
// */
//@Getter
//public class BaseResult<T> implements Result<T>, Serializable {
//    private static final long serialVersionUID = 1L;
//    protected String cacheName;
//    protected CacheOutcome outcome;
//    protected T value;
//    protected Throwable error;
//
//    BaseResult(String cacheName,
//               CacheOutcome outcome,
//               T value,
//               Throwable error) {
//        this.cacheName = Objects.requireNonNull(cacheName, "agentName");
//        this.outcome = Objects.requireNonNull(outcome, "outcome");
//        this.value = value;
//        this.error = error;
//    }
//
//    @Override
//    public CacheOutcome outcome() {
//        return outcome;
//    }
//
//    @Override
//    public T value() {
//        return value;
//    }
//
//    @Override
//    public Throwable error() {
//        return error;
//    }
//}
