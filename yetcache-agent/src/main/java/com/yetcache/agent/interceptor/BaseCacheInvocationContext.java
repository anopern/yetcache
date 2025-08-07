//package com.yetcache.agent.interceptor;
//
//import com.yetcache.agent.core.StructureType;
//import lombok.Data;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author walter.yan
// * @since 2025/7/13
// */
//@Data
//public abstract class BaseCacheInvocationContext implements CacheInvocationContext {
//    protected final String componentNane;
//    protected final String methodName;
//    protected final StructureType structureType;
//    protected final BehaviorType behaviorType;
//    protected final Map<String, Object> attributes = new ConcurrentHashMap<>();
//
//    public BaseCacheInvocationContext(String componentNane,
//                                      String methodName,
//                                      StructureType structureType,
//                                      BehaviorType behaviorType) {
//        this.componentNane = componentNane;
//        this.methodName = methodName;
//        this.structureType = structureType;
//        this.behaviorType = behaviorType;
//    }
//
//    public <T> void setAttribute(String key, T value) {
//        attributes.put(key, value);
//    }
//
//    @SuppressWarnings("unchecked")
//    public <T> T getAttribute(String key) {
//        return (T) attributes.get(key);
//    }
//
//    @Override
//    public String componentNane() {
//        return componentNane;
//    }
//
//    @Override
//    public StructureType structureType() {
//        return structureType;
//    }
//
//    @Override
//    public BehaviorType behaviorType() {
//        return behaviorType;
//    }
//}
