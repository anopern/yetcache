package com.yetcache.agent.core.structure.hash.get;

import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.interceptor.BehaviorType;
import com.yetcache.agent.interceptor.CacheInvocationCommand;
import com.yetcache.agent.interceptor.StructureBehaviorKey;
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
public class HashCacheAgentGetInvocationCommand implements CacheInvocationCommand {
    private String componentName;
    private Object bizKey;
    private Object bizField;

    public static HashCacheAgentGetInvocationCommand of(String componentName, Object bizKey, Object bizField) {
        return HashCacheAgentGetInvocationCommand.builder()
                .componentName(componentName)
                .bizKey(bizKey)
                .bizField(bizField)
                .build();
    }

    @Override
    public String componentName() {
        return this.componentName;
    }

    @Override
    public StructureBehaviorKey structureBehaviorKey() {
        return StructureBehaviorKey.of(StructureType.HASH, BehaviorType.GET);
    }
}
