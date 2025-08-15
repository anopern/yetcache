package com.yetcache.core.codec;

import java.lang.reflect.Type;

/**
 * @author walter.yan
 * @since 2025/8/12
 */
public interface ValueStringCodec {
    String encode(Object value) throws Exception;

    Object decode(String json, Type valueType) throws Exception;
}
