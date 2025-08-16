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
    private String keyTypeId;
    private String fieldTypeId;
    private String valueTypeId;

    private TypeRef<?> keyTypeRef;
    private TypeRef<?> fieldTypeRef;
    private TypeRef<?> valueTypeRef;

    public static TypeDescriptor of(TypeRef<?> keyTypeRef, TypeRef<?> fieldTypeRef, TypeRef<?> valueTypeRef) {
        return TypeDescriptor.builder()
                .keyTypeId(toTypeId(keyTypeRef))
                .fieldTypeId(toTypeId(fieldTypeRef))
                .valueTypeId(toTypeId(valueTypeRef))
                .keyTypeRef(keyTypeRef)
                .fieldTypeRef(fieldTypeRef)
                .valueTypeRef(valueTypeRef)
                .build();
    }

    public static String toTypeId(TypeRef<?> typeRef) {
        Type type = typeRef.getType();
        // 你可以这里自定义规则，比如去掉泛型参数、简化包名等
        return type.getTypeName();
    }
}
