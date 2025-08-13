package com.yetcache.core.cache;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import java.lang.reflect.Type;

/**
 * @author walter.yan
 * @since 2025/8/13
 */
@AllArgsConstructor
public class ObjectMapperCodec implements ValueCodec {
    private final ObjectMapper objectMapper;

    @Override
    public byte[] encode(Object value, Type valueType) throws Exception {
        return objectMapper.writeValueAsBytes(value);
    }

    @Override
    public Object decode(byte[] bytes, Type valueType) throws Exception {
        if (bytes != null && bytes.length > 0) {
            JavaType javaType = objectMapper.getTypeFactory().constructType(valueType);
            return objectMapper.readValue(bytes, javaType);
        }
        return null;
    }
}
