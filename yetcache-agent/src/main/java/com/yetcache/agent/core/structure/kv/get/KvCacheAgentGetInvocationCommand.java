package com.yetcache.agent.core.structure.kv.get;

import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.core.BehaviorType;
import com.yetcache.agent.interceptor.CacheInvocationCommand;
import com.yetcache.agent.core.StructureBehaviorKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KvCacheAgentGetInvocationCommand implements CacheInvocationCommand {
    private String cacheAgentName;
    private Object bizKey;

    public static KvCacheAgentGetInvocationCommand of(String componentName, Object bizKey) {
        return KvCacheAgentGetInvocationCommand.builder()
                .cacheAgentName(componentName)
                .bizKey(bizKey)
                .build();
    }

    @Override
    public String cacheAgentName() {
        return this.cacheAgentName;
    }

    @Override
    public StructureBehaviorKey sbKey() {
        return StructureBehaviorKey.of(StructureType.HASH, BehaviorType.GET);
    }
}
