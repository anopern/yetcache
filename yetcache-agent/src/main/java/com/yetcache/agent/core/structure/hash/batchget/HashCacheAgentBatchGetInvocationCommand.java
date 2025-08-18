package com.yetcache.agent.core.structure.hash.batchget;

import com.yetcache.agent.core.StructureType;
import com.yetcache.agent.interceptor.BehaviorType;
import com.yetcache.agent.interceptor.CacheInvocationCommand;
import com.yetcache.agent.interceptor.StructureBehaviorKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/8/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HashCacheAgentBatchGetInvocationCommand implements CacheInvocationCommand {
    private String componentName;
    private Object bizKey;
    private List<Object> bizFields;

    @Override
    public String componentName() {
        return this.componentName;
    }

    @Override
    public StructureBehaviorKey structureBehaviorKey() {
        return StructureBehaviorKey.of(StructureType.HASH, BehaviorType.BATCH_GET);
    }

    public static HashCacheAgentBatchGetInvocationCommand of(String componentName, Object bizKey, List<Object> bizFields) {
        return new HashCacheAgentBatchGetInvocationCommand(componentName, bizKey, bizFields);
    }
}
