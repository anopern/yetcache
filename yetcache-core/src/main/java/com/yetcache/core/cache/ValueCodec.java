package com.yetcache.core.cache;

import java.lang.reflect.Type;

/**
 * @author walter.yan
 * @since 2025/8/12
 */
public interface ValueCodec {
    String encode(Object value) throws Exception;

    Object decode(String json, Type valueType) throws Exception;
}
