package com.yetcache.utils;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;

/**
 * @author walter.yan
 * @since 2025/5/21
 */
@Slf4j
public class LockUtils {
    public static void safeUnlock(RLock lock) {
        if (lock != null && lock.isHeldByCurrentThread()) {
            try {
                lock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.warn("Unlock failed", e);
            }
        }
    }
}
