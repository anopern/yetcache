package com.yetcache.core.codec;

/**
 * @author walter.yan
 * @since 2025/8/15
 */
public interface WrapperReifier<T> {
    Class<T> targetType();

    T reify(T wrapper, ReifyContext ctx) throws Exception;

    Object reifySlot(Object raw, TypeRef<?> valueType) throws Exception;
}
