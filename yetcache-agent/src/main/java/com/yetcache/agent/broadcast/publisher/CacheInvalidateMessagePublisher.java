package com.yetcache.agent.broadcast.publisher;

import com.yetcache.agent.broadcast.CacheInvalidateCommand;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
public interface CacheInvalidateMessagePublisher {
    void publish(CacheInvalidateCommand command);
}
