package com.yetcache.agent.broadcast.receiver.handler;

import com.yetcache.agent.broadcast.command.CacheUpdateCommand;
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

    public Optional<CacheBroadcastHandler> getHandler(CacheUpdateCommand cmd) {
        return handlers.stream()
                .filter(h -> h.supports(cmd))
                .findFirst();
    }
}