package com.yetcache.core.cache.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/7
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HashCacheSingleGetCommand {
    private Object bizKey;
    private Object bizField;
}
