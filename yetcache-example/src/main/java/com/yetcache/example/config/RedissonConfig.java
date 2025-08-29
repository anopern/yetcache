package com.yetcache.example.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@Configuration
public class RedissonConfig {
    @Bean
    @Profile("fins")
    public RedissonClient redissonClient() {
        Config config = new Config();

        config.useClusterServers()
                .addNodeAddress("redis://10.60.8.47:6383")
                .addNodeAddress("redis://10.60.8.48:6384")
                .addNodeAddress("redis://10.60.8.49:6384")
                .addNodeAddress("redis://10.60.8.48:6383")
                .addNodeAddress("redis://10.60.8.49:6383")
                .addNodeAddress("redis://10.60.8.47:6384")
                .setPassword("xpMj4KymXLe5")
                // 连接池配置
                .setMasterConnectionPoolSize(64)
                .setSlaveConnectionPoolSize(64)
                .setMasterConnectionMinimumIdleSize(10)
                .setSlaveConnectionMinimumIdleSize(10)

                // 超时配置
                .setConnectTimeout(10000)
                .setTimeout(3000)
                .setRetryAttempts(3)
                .setRetryInterval(1500);

        config.setCodec(new Fastjson2Codec());

        return Redisson.create(config);
    }

}