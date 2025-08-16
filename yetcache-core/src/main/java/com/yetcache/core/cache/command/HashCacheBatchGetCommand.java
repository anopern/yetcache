package com.yetcache.core.cache.command;

import com.yetcache.core.codec.TypeRef;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/8/7
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HashCacheBatchGetCommand {
    private Object bizKey;
    private List<Object> bizFields;

    private TypeRef<?> valueTypeRef;

    @SuppressWarnings("unchecked")
    public <T> TypeRef<T> valueTypeRef() { return (TypeRef<T>) valueTypeRef; }
}
