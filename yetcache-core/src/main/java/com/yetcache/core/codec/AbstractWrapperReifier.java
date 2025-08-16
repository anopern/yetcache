package com.yetcache.core.codec;

import lombok.AllArgsConstructor;

/**
 * @author walter.yan
 * @since 2025/8/15
 */
@AllArgsConstructor
public abstract class AbstractWrapperReifier<T> implements WrapperReifier<T> {
    protected final JsonTypeConverter jsonTypeConverter;

    @Override
    public Object reifySlot(Object raw, TypeRef<?> typeRef) throws Exception {
        if (raw == null || typeRef == null || typeRef.isInstance(raw)) {
            return raw;
        }
        return jsonTypeConverter.convert(raw, typeRef.getType());
    }
}
