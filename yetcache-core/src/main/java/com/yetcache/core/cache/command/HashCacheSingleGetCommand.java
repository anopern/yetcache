package com.yetcache.core.cache.command;

import com.yetcache.core.codec.TypeDescriptor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/7
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HashCacheSingleGetCommand {
    private Object bizKey;
    private Object bizField;
    private TypeDescriptor typeDesc;
}
