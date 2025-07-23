package com.yetcache.agent.broadcast.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DynamicHashCacheBroadcastCommand<K, F, V> extends AbstractCacheBroadcastCommand {
    protected Map<K, Map<F, V>> data;
}
