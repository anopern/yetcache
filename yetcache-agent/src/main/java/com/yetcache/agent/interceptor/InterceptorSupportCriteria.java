package com.yetcache.agent.interceptor;

import com.yetcache.agent.agent.StructureBehaviorKey;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/8/31
 */
@AllArgsConstructor
@Data
public class InterceptorSupportCriteria {
    private final StructureBehaviorKey sbKey;
    private final String cacheAgentName;

    public static InterceptorSupportCriteria of(StructureBehaviorKey sbKey, String cacheAgentName) {
        return new InterceptorSupportCriteria(sbKey, cacheAgentName);
    }
}
