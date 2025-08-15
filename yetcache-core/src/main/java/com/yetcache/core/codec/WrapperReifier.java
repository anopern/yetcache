package com.yetcache.core.codec;

/**
 * @author walter.yan
 * @since 2025/8/15
 */
public interface WrapperReifier<T> {
    Class<T> targetType();
    T reify(T wrapper, TypeRef<?> valueType, JsonTypeConverter converter) throws Exception;
}
