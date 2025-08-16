package com.yetcache.core.codec;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author walter.yan
 * @since 2025/8/16
 */
public class TypeRefRegistry {
    private final Map<String, TypeRef<?>> typeRefMap = new ConcurrentHashMap<>();

    public TypeRefRegistry() {
        typeRefMap.put(TypeDescriptor.toTypeId(TypeRef.of(String.class)), TypeRef.of(String.class));
        typeRefMap.put(TypeDescriptor.toTypeId(TypeRef.of(Integer.class)), TypeRef.of(Integer.class));
        typeRefMap.put(TypeDescriptor.toTypeId(TypeRef.of(Long.class)), TypeRef.of(Long.class));
        typeRefMap.put(TypeDescriptor.toTypeId(TypeRef.of(Double.class)), TypeRef.of(Double.class));
        typeRefMap.put(TypeDescriptor.toTypeId(TypeRef.of(Float.class)), TypeRef.of(Float.class));
        typeRefMap.put(TypeDescriptor.toTypeId(TypeRef.of(Boolean.class)), TypeRef.of(Boolean.class));
    }

    public void register(String typeId, TypeRef<?> typeRef) {
        typeRefMap.put(typeId, typeRef);
    }

    public TypeRef<?> get(String typeId) {
        return typeRefMap.get(typeId);
    }
}
