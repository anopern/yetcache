package com.yetcache.agent.broadcast.receiver;

import com.yetcache.agent.broadcast.command.AbstractCacheBroadcastCommand;
import com.yetcache.agent.broadcast.handler.CacheBroadcastHandler;
import com.yetcache.agent.broadcast.handler.CacheBroadcastHandlerRegistry;
import com.yetcache.agent.regitry.CacheAgentRegistry;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
@Slf4j
public class DefaultCacheBroadcastReceiver implements CacheBroadcastReceiver {
    private final CacheBroadcastHandlerRegistry handlerRegistry;

    public DefaultCacheBroadcastReceiver(CacheAgentRegistry registry, CacheBroadcastHandlerRegistry handlerRegistry) {
        this.handlerRegistry = handlerRegistry;
    }

    @Override
    public void onReceive(AbstractCacheBroadcastCommand cmd) {
        Optional<CacheBroadcastHandler> optional = handlerRegistry.getHandler(cmd);
        if (optional.isPresent()) {
            optional.get().handle(cmd);
        } else {
            log.error("[YetCache] No broadcast handler for: " + cmd);
        }
    }
}
