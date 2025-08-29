package com.yetcache.agent.agent;

import com.yetcache.core.config.CacheLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/29
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CacheAgentPutOptions {
    private CacheLevel cacheLevel;

    public static CacheAgentPutOptions of(CacheLevel cacheLevel) {
        return new CacheAgentPutOptions(cacheLevel);
    }
}
