package com.yetcache.agent.broadcast.command;

import com.yetcache.agent.broadcast.InstanceIdProvider;
import com.yetcache.agent.broadcast.command.descriptor.CommandDescriptor;
import com.yetcache.agent.core.CacheAgentMethod;
import com.yetcache.agent.core.CacheStructureType;
import lombok.AllArgsConstructor;
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
public class ExecutableCommand {
    private  CommandDescriptor descriptor;
    private  Map<String, Object> payload;

    public <T> T getPayloadAs(String key, Class<T> type) {
        Object value = payload.get(key);
        if (value == null) {
            return null;
        }
        if (!type.isInstance(value)) {
            throw new IllegalArgumentException("Payload value type mismatch for key: " + key);
        }
        return type.cast(value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getPayloadRaw(String key) {
        return (T) payload.get(key);
    }

    public static <K, F, V> ExecutableCommand dynamicHash(String agentName, CacheAgentMethod action, K bizKey,
                                                          Map<F, V> valueMap) {
        CommandDescriptor descriptor = new CommandDescriptor(CacheStructureType.DYNAMIC_HASH,
                agentName,
                action,
                InstanceIdProvider.getInstanceId());
        Map<String, Object> payload = TypedPayloadResolver.serialize(bizKey, valueMap);
        return new ExecutableCommand(descriptor, payload);
    }
}
