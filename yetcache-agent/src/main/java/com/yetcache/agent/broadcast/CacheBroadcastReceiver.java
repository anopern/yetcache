package com.yetcache.agent.broadcast;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
public interface CacheBroadcastReceiver {
    void onReceive(AbstractCacheBroadcastCommand cmd);
}
