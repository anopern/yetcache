package com.yetcache.agent.broadcast.command.playload;

import com.yetcache.agent.interceptor.BehaviorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/8/15
 */
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class HashPlayload {
    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static final class FieldValue {
        private String field;
        private Object value;

        public static FieldValue of(String field, Object value) {
            return new FieldValue(field, value);
        }
    }

    private String valueTypeId;

    private String key;
    private List<FieldValue> fieldValues;
}
