package com.yetcache.agent.broadcast.command;

import com.yetcache.agent.broadcast.command.descriptor.CommandDescriptor;

import java.util.Map;
import java.util.Objects;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
public class ExecutableCommand {
    private final CommandDescriptor descriptor;
    private final Map<String, Object> payload;

    public ExecutableCommand(CommandDescriptor descriptor, Map<String, Object> payload) {
        this.descriptor = Objects.requireNonNull(descriptor);
        this.payload = payload == null ? Map.of() : payload;
    }

    public <T> T getPayloadAs(String key, Class<T> type) {
        Object value = payload.get(key);
        if (value == null) return null;
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException("Payload value type mismatch for key: " + key);
        }
        return type.cast(value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPayloadRaw(String key) {
        return (T) payload.get(key);
    }
}
