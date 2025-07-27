package com.yetcache.agent.broadcast.receiver;

/**
 * @author walter.yan
 * @since 2025/7/27
 */
public interface CacheBroadcastReceiver {
    void onMessage(String message);
}
