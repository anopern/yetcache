package com.yetcache.agent.broadcast.command;

import lombok.*;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/8/12
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Playload {
    private Map<Object, Object> bizKeyValueMap;

    private Object bizKey;
    private Map<Object, Object> bizFieldValueMap;
}
