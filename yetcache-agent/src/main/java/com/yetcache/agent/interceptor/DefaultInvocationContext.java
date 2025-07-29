package com.yetcache.agent.interceptor;

import com.yetcache.agent.core.StructureType;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author walter.yan
 * @since 2025/7/13
 */
@Data
public class DefaultInvocationContext implements InvocationContext {
    private final String componentNane;
    private final String methodName;
    private final StructureType structureType;
    private final BehaviorType behaviorType;
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    public DefaultInvocationContext(String componentNane,
                                    String methodName,
                                    StructureType structureType,
                                    BehaviorType behaviorType) {
        this.componentNane = componentNane;
        this.methodName = methodName;
        this.structureType = structureType;
        this.behaviorType = behaviorType;
    }

    public <T> void setAttribute(String key, T value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }
}
