package com.yetcache.agent.broadcast.command.playload;

import com.yetcache.agent.interceptor.BehaviorType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/8/15
 */
@AllArgsConstructor
@Data
public class KvPlayload {
    @AllArgsConstructor
    @Data
    public static final class KeyValue {
        private Object key;
        private Object value;
    }

    private final BehaviorType behaviorType;

    private final String keyTypeId;
    private final String valueTypeId;

    private final List<KeyValue> keyValues;
}
