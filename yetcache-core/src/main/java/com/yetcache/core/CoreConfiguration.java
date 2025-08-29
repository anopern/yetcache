package com.yetcache.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetcache.core.codec.JsonTypeConverter;
import com.yetcache.core.codec.TypeRefRegistry;
import com.yetcache.core.codec.jackson.JacksonJsonTypeConvertor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author walter.yan
 * @since 2025/8/16
 */
@Configuration
public class CoreConfiguration {
    @Bean
    public JsonTypeConverter jsonTypeConverter(ObjectMapper objectMapper) {
        return new JacksonJsonTypeConvertor(objectMapper);
    }

    @Bean
    public TypeRefRegistry typeRefRegistry() {
        return new TypeRefRegistry();
    }

}
