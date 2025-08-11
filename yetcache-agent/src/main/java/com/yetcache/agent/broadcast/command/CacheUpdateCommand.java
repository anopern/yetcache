package com.yetcache.agent.broadcast.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CacheUpdateCommand {
    private CommandDescriptor descriptor;
    private Playload payload;
    private Long createdTime;
}
