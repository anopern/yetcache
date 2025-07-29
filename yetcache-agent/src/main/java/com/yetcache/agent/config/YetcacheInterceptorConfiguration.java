package com.yetcache.agent.config;

import com.yetcache.agent.core.structure.dynamichash.get.DynamicHashCacheGetInterceptor;
import com.yetcache.agent.interceptor.InvocationChainRegistry;
import com.yetcache.core.config.YetCacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author walter.yan
 * @since 2025/7/30
 */
@Configuration
@EnableConfigurationProperties(YetCacheProperties.class)
public class YetcacheInterceptorConfiguration {
    @Bean
    public InvocationChainRegistry invocationChainRegistry(YetCacheProperties props) {

        DynamicHashCacheGetInterceptor<?,?,?> interceptor = new DynamicHashCacheGetInterceptor<>(
                "dynamicHashCache",
                props.getCaches().getDynamicHashCache(),
                props.getCaches().getDynamicHashCache().getConfig(),
                props.getCaches().getDynamicHashCache().getLoader(),
                null,
                null
        );
        return new InvocationChainRegistry(props.getInterceptors());
    }
}
