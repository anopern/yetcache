package com.yetcache.agent.broadcast;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CacheRemoveCommand {
    private String structureType;
    protected String cacheAgentName;
    protected String key;
    protected String instanceId;
    private Long publishAt;
    protected Map<String, String> extra;
}
