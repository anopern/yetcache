package com.yetcache.agent.broadcast.publisher;

import com.yetcache.agent.broadcast.command.CacheCommand;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
public interface CacheBroadcastPublisher {
    void publish(CacheCommand command);
}
