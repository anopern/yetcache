package com.yetcache.agent.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author walter.yan
 * @since 2025/8/11
 */
@AllArgsConstructor
@Builder
@Getter
@ToString
public class PutAllOptions {
    @Builder.Default
    private final boolean broadcast = true;

    public static PutAllOptions defaultOptions() {
        return PutAllOptions.builder().build();
    }
}
