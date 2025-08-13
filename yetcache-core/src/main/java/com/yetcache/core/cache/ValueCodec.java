package com.yetcache.core.cache;

import java.lang.reflect.Type;

/**
 * @author walter.yan
 * @since 2025/8/12
 */
public interface ValueCodec {
    byte[] encode(Object value, Type valueType) throws Exception;

    Object decode(byte[] bytes, Type valueType) throws Exception;
}
