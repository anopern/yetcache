package com.yetcache.core.cache;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Type;

/**
 * @author walter.yan
 * @since 2025/8/13
 */
@AllArgsConstructor
@Getter
public class JacksonValueCodec implements ValueCodec {
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
}
