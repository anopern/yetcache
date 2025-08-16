package com.yetcache.core.codec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Type;

/**
 * @author walter.yan
 * @since 2025/8/12
 */
@AllArgsConstructor
@Data
@Builder
public class TypeDescriptor {
    private String valueTypeId;

    private TypeRef<?> valueTypeRef;

    public static TypeDescriptor of(TypeRef<?> valueTypeRef) {
        return TypeDescriptor.builder()
                .valueTypeId(toTypeId(valueTypeRef))
                .valueTypeRef(valueTypeRef)
                .build();
    }

    public static String toTypeId(TypeRef<?> typeRef) {
        Type type = typeRef.getType();
        // 你可以这里自定义规则，比如去掉泛型参数、简化包名等
        return type.getTypeName();
    }
}
