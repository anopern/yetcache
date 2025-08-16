package com.yetcache.core.cache.command;

import com.yetcache.core.codec.TypeRef;
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

    private TypeRef<?> valueTypeRef;

    public static <T> HashCacheSingleGetCommand of(Object bizKey, Object bizField, TypeRef<T> ref) {
        return new HashCacheSingleGetCommand(bizKey, bizField, ref);
    }

    // ✅ 泛型访问器：在使用点把 <?> 进化回 <T>
    @SuppressWarnings("unchecked")
    public <T> TypeRef<T> valueTypeRef() { return (TypeRef<T>) valueTypeRef; }
}
