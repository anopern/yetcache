package com.yetcache.core.codec;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author walter.yan
 * @since 2025/8/15
 */
public final class TypeRefRegistry {
    private final Map<String, TypeRef<?>> typeRefMap = new ConcurrentHashMap<>();

    public void register(String typeId, TypeRef<?> typeRef) {
        typeRefMap.put(typeId, typeRef);
    }

    public Optional<TypeRef<?>> get(String typeId) {
        return Optional.ofNullable(typeRefMap.get(typeId));
    }
}
