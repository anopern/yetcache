package com.yetcache.core.cache;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/8/12
 */
@AllArgsConstructor
@Data
public class TypeDescriptor {
    private final TypeRef<?> valueTypeRef;
}
