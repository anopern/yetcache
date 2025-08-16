package com.yetcache.agent.broadcast.command.playload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/8/15
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class KvPlayload {
    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static final class KeyValue {
        private String key;
        private Object value;
    }

    private String valueTypeId;

    private List<KeyValue> keyValues;
}
