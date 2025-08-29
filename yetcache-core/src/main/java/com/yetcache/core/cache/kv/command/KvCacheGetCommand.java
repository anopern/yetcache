package com.yetcache.core.cache.kv.command;

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
public class KvCacheGetCommand {
    private Object bizKey;
    private TypeRef<?> valueTypeRef;

    public static <T> KvCacheGetCommand of(Object bizKey, TypeRef<T> ref) {
        return new KvCacheGetCommand(bizKey, ref);
    }

    // ✅ 泛型访问器：在使用点把 <?> 进化回 <T>
    @SuppressWarnings("unchecked")
    public <T> TypeRef<T> valueTypeRef() { return (TypeRef<T>) valueTypeRef; }
}
