package com.yetcache.core.codec;

import com.yetcache.core.cache.support.CacheValueHolder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author walter.yan
 * @since 2025/8/16
 */
public class TypeRefs {
    // 简单的 ParameterizedType 实现
    static final class ParamType implements ParameterizedType {
        private final Class<?> raw;
        private final Type[] args;
        ParamType(Class<?> raw, Type... args) {
            this.raw = raw; this.args = args;
        }
        public Type[] getActualTypeArguments(){ return args; }
        public Type getRawType(){ return raw; }
        public Type getOwnerType(){ return null; }
        public String toString() {
            return raw.getTypeName() + "<" +
                    java.util.Arrays.stream(args).map(Type::getTypeName)
                            .reduce((a,b)->a+","+b).orElse("") + ">";
        }
    }

    // 核心：由 valueRef 生成 holderRef
    public static <T> TypeRef<CacheValueHolder<T>> holderOf(TypeRef<T> valueRef) {
        final Type arg = valueRef.getType();
        return new TypeRef<CacheValueHolder<T>>() {
            @Override public Type getType() {
                return new ParamType(CacheValueHolder.class, arg);
            }
        };
    }

    // 便民：Class<T> -> TypeRef<T>
    public static <T> TypeRef<T> of(Class<T> cls) {
        return new TypeRef<T>() {
            @Override public Type getType() { return cls; }
        };
    }
}
