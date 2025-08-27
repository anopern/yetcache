package com.yetcache.agent.broadcast.receiver.handler;

import com.yetcache.agent.core.StructureType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
public class CacheBroadcastHandlerRegistry {
    private final List<CacheBroadcastHandler> handlers = new ArrayList<>();

    public void register(CacheBroadcastHandler handler) {
        handlers.add(handler);
    }

    public Optional<CacheBroadcastHandler> getHandler(StructureType structureType) {
        return handlers.stream()
                .filter(h -> h.supports(structureType))
                .findFirst();
    }
}