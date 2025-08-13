package com.yetcache.core.cache;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author walter.yan
 * @since 2025/8/13
 */
public abstract class TypeRef<T> {
    private final Type type;

    protected TypeRef() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) superClass;
            this.type = parameterizedType.getActualTypeArguments()[0];
        } else {
            throw new IllegalArgumentException("Invalid TypeRef construction");
        }
    }

    public Type getType() {
        return type;
    }
}
