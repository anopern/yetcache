package com.yetcache.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author walter.yan
 * @since 2025/7/28
 */
public final class YetCacheSerializationCenter {

    private static final ObjectMapper defaultMapper;

    static {
        defaultMapper = new ObjectMapper();
        defaultMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        defaultMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        defaultMapper.registerModule(new JavaTimeModule());
    }

    public static ObjectMapper getMapper() {
        return defaultMapper;
    }

    public static void override(ObjectMapper custom) {
        // 支持业务替换 mapper，但平台内部只通过中心获取
    }
}

