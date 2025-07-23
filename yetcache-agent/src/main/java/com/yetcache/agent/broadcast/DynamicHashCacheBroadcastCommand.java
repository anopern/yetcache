package com.yetcache.agent.broadcast;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.function.Function;

/**
 * @author walter.yan
 * @since 2025/7/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DynamicHashCacheBroadcastCommand extends AbstractCacheBroadcastCommand {
    protected String field;

    public <F> F parseField(Function<String, F> parser) {
        return parser.apply(this.field);
    }
}
