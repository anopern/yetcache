package com.yetcache.agent.broadcast.publisher;

import com.yetcache.agent.broadcast.command.CacheRemoveCommand;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
public interface CacheBroadcastPublisher {
    void publish(CacheRemoveCommand command);
}
