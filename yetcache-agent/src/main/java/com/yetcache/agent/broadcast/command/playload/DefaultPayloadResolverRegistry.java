package com.yetcache.agent.broadcast.command.playload;

import com.yetcache.agent.broadcast.command.descriptor.CommandDescriptor;
import com.yetcache.agent.broadcast.command.descriptor.CommandDescriptorKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
public class DefaultPayloadResolverRegistry implements PayloadResolverRegistry {
    private final Map<CommandDescriptorKey, PayloadResolver<?>> registry = new ConcurrentHashMap<>();

    @Override
    public void register(CommandDescriptorKey key, PayloadResolver<?> resolver) {
        if (registry.containsKey(key)) {
            throw new IllegalStateException("Duplicate resolver for key: " + key);
        }
        registry.put(key, resolver);
    }

    @Override
    public PayloadResolver<?> get(CommandDescriptor descriptor) {
        CommandDescriptorKey key = new CommandDescriptorKey(
                descriptor.getStructureType(),
                descriptor.getAction()
        );

        PayloadResolver<?> resolver = registry.get(key);
        if (resolver == null) {
            throw new IllegalArgumentException("No PayloadResolver found for key: " + key);
        }
        return resolver;
    }
}
