package com.yetcache.core.cache;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetcache.core.codec.ValueStringCodec;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Type;

/**
 * @author walter.yan
 * @since 2025/8/13
 */
@AllArgsConstructor
@Getter
public class JacksonValueStringCodec implements ValueStringCodec {
    private final ObjectMapper objectMapper;

    @Override
    public String encode(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    @Override
    public Object decode(String json, Type valueType) throws Exception {
        if (StrUtil.isNotBlank(json)) {
            JavaType javaType = objectMapper.getTypeFactory().constructType(valueType);
            return objectMapper.readValue(json, javaType);
        }
        return null;
    }

    @Override
    public Object convert(Object obj, Type targetType) throws Exception {
        if (null != obj) {
            JavaType javaType = objectMapper.getTypeFactory().constructType(targetType);
            return objectMapper.convertValue(obj, javaType);
        }
        return null;
    }
}
