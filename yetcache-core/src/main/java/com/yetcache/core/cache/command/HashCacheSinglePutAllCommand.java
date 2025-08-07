package com.yetcache.core.cache.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


/**
 * @author walter.yan
 * @since 2025/7/30
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HashCacheSinglePutAllCommand {
    protected Object bizKey;
    protected Map<Object, Object> valueMap;

    protected Long localLogicTtlSecs;
    protected Long localPhysicalTtlSecs;
    protected Long remoteLogicTtlSecs;
    protected Long remotePhysicalTtlSecs;
}
