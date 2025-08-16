package com.yetcache.core.codec;

import java.lang.reflect.Type;

/**
 * @author walter.yan
 * @since 2025/8/12
 */
public interface JsonValueCodec {
    String encode(Object value) throws Exception;

    <T> T  decode(String json, Type valueType) throws Exception;
}
