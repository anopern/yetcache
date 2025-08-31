package com.yetcache.agent.broadcast.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetcache.agent.broadcast.CacheInvalidateCommand;
import com.yetcache.core.config.RedisPubSubConfig;
import lombok.AllArgsConstructor;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

/**
 * @author walter.yan
 * @since 2025/8/31
 */
@AllArgsConstructor
public class RedisInvalidateMessagePublisher implements CacheInvalidateMessagePublisher {
    private final RedisPubSubConfig config;
    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(CacheInvalidateCommand command) {
        try {
            String topicName = config.getTopic();
            RTopic topic = redissonClient.getTopic(topicName);
            topic.publish(toJson(command));
        } catch (Exception e) {
            throw new RuntimeException("Failed to broadcast cache command via Redis: " + command, e);
        }
    }

    private String toJson(CacheInvalidateCommand command) throws JsonProcessingException {
        return objectMapper.writeValueAsString(command);
    }
}














