package com.yetcache.agent.core.port;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/8/11
 */
public interface HashCacheFillPort {
    void fillAndBroadcast(Object bizKey, Map<Object, Object> valueMap);
}
