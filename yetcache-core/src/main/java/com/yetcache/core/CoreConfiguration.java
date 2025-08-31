package com.yetcache.core;

import com.yetcache.core.codec.TypeRefRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author walter.yan
 * @since 2025/8/16
 */
@Configuration
public class CoreConfiguration {

    @Bean
    public TypeRefRegistry typeRefRegistry() {
        return new TypeRefRegistry();
    }

}
