package com.yetcache.core.cache.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author walter.yan
 * @since 2025/7/30
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HashCacheSinglePutCommand {
    protected Object bizKey;
    protected Object bizField;
    protected Object value;

    protected Long localLogicTtlSecs;
    protected Long localPhysicalTtlSecs;
    protected Long remoteLogicTtlSecs;
    protected Long remotePhysicalTtlSecs;
}
