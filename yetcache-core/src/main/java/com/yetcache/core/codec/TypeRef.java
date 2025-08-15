package com.yetcache.core.codec;

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

    public boolean isInstance(Object obj) {
        if (type instanceof Class<?>) {
            return ((Class<?>) type).isInstance(obj);
        }
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType instanceof Class<?>) {
                return ((Class<?>) rawType).isInstance(obj);
            }
        }
        // 其他 Type 情况可以按需补充，比如 GenericArrayType、WildcardType
        return false;
    }
}
