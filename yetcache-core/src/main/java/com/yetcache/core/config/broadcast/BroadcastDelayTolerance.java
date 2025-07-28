package com.yetcache.core.config.broadcast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/7/28
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class BroadcastDelayTolerance {
    private Long maxDelaySecs;
    private DelayExceededPolicy delayExceededPolicy;

    public BroadcastDelayTolerance defaultPolicy() {
        return new BroadcastDelayTolerance(10L, DelayExceededPolicy.INVALIDATE);
    }
}
