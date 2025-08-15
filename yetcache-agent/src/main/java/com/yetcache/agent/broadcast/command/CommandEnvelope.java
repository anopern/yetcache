package com.yetcache.agent.broadcast.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/8/14
 */
@Data
public class CommandEnvelope {
    private CommandDescriptor descriptor;
    private JsonNode jsonNode;
}
