package com.yetcache.example.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        config.useSingleServer()
                .setAddress("redis://10.60.8.47:6383")
                .setAddress("redis://10.60.8.48:6384")
                .setPassword("xpMj4KymXLe5")
                .setConnectionMinimumIdleSize(10)
                .setConnectionPoolSize(64)
                .setIdleConnectionTimeout(10000);

        config.setCodec(new Fastjson2Codec());

        return Redisson.create(config);
    }

}