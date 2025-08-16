package com.yetcache.agent.broadcast.command.playload;

import com.yetcache.agent.interceptor.BehaviorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/8/15
 */
@AllArgsConstructor
@Data
@Builder
public class HashPlayload {
    @AllArgsConstructor
    @Data
    public static final class FieldValue {
        private Object field; // 包含 fieldTypeId
        private Object value; // PUT 时携带 valueTypeId

        public static FieldValue of(Object field, Object value) {
            return new FieldValue(field, value);
        }
    }

    private BehaviorType behaviorType;

    private String keyTypeId;
    private String fieldTypeId;
    private String valueTypeId;

    private Object key;
    private List<FieldValue> fieldValues;
}
