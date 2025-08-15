package com.yetcache.agent.broadcast.command;

import com.yetcache.agent.interceptor.StructureBehaviorKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommandDescriptor {
    protected String componentName;
    protected StructureBehaviorKey structureBehaviorKey;
    protected String instanceId;
    private Long createdTime;

    protected Map<String, String> extra;

    public CommandDescriptor(String componentName,
                             StructureBehaviorKey structureBehaviorKey,
                             String instanceId,
                             Long createdTime) {
        this(componentName, structureBehaviorKey, instanceId, createdTime, null);
    }
}
