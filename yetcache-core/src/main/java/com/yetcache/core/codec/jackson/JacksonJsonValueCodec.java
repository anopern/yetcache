package com.yetcache.core.codec.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetcache.core.codec.JsonValueCodec;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;

/**
 * @author walter.yan
 * @since 2025/8/13
 */
@AllArgsConstructor
@Getter
public class JacksonJsonValueCodec implements JsonValueCodec {
    private final ObjectMapper objectMapper;

    @Override
    public String encode(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    @Override
    public Object decode(String json, Type valueType) throws Exception {
        if (StringUtils.isNotBlank(json)) {
            JavaType javaType = objectMapper.getTypeFactory().constructType(valueType);
            return objectMapper.readValue(json, javaType);
        }
        return null;
    }
}
