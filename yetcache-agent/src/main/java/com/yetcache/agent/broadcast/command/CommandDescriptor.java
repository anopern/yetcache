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
    private String shape;
    protected String componentName;
    protected StructureBehaviorKey sbKey;
    protected String instanceId;
    private Long publishAt;
    protected Map<String, String> extra;
}
