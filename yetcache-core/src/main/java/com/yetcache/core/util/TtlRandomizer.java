package com.yetcache.core.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

import static com.yetcache.core.util.CacheConstants.MAX_TTL_RANDOMIZE_PERCENT;

/**
 * @author walter.yan
 * @since 2025/6/29
 */
@Slf4j
public class TtlRandomizer {
    public static long randomizeSecs(long baseTtlSecs, double percent) {
        if (baseTtlSecs <= 0 || percent <= 0) {
            return baseTtlSecs;
        }
        if (percent > MAX_TTL_RANDOMIZE_PERCENT) {
            log.warn("TTL randomize percent {} exceeds max limit {}, fallback to max", percent, MAX_TTL_RANDOMIZE_PERCENT);
            percent = MAX_TTL_RANDOMIZE_PERCENT;
        }
        long delta = (long) (baseTtlSecs * percent);
        long randomOffset = ThreadLocalRandom.current().nextLong(-delta, delta + 1);
        return Math.max(1, baseTtlSecs + randomOffset);
    }
}
