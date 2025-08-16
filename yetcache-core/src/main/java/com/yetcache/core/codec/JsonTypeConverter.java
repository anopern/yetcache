package com.yetcache.core.codec;

import java.lang.reflect.Type;

/**
 * @author walter.yan
 * @since 2025/8/15
 */
public interface JsonTypeConverter {
    <T> T convert(Object raw, Type target) throws Exception;
}
