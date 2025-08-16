package com.yetcache.core.codec.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetcache.core.codec.JsonTypeConverter;
import lombok.AllArgsConstructor;

import java.lang.reflect.Type;

/**
 * @author walter.yan
 * @since 2025/8/15
 */
@AllArgsConstructor
public class JacksonJsonTypeConvertor implements JsonTypeConverter {
    private final ObjectMapper objectMapper;

    @Override
    public <T> T convert(Object raw, Type target) throws Exception {
        if (null != raw) {
            JavaType javaType = objectMapper.getTypeFactory().constructType(target);
            return objectMapper.convertValue(raw, javaType);
        }
        return null;
    }
}
