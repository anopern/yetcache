package com.yetcache.agent.broadcast.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetcache.agent.agent.BehaviorType;
import com.yetcache.agent.agent.CacheAgentPortRegistry;
import com.yetcache.agent.agent.kv.port.KvCacheAgentRemovePort;
import com.yetcache.agent.broadcast.CacheInvalidateCommand;
import com.yetcache.core.config.RedisPubSubConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

/**
 * @author walter.yan
 * @since 2025/8/31
 */
@AllArgsConstructor
@Slf4j
public class RedisCacheInvalidateMessageSubscriber implements CacheInvalidateMessageSubscriber {
    private final RedisPubSubConfig config;
    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;
    private final CacheAgentPortRegistry agentPortRegistry;

    @Override
    public void subscribe() {
        RTopic topic = redissonClient.getTopic(config.getTopic());
        topic.addListener(String.class, (channel, msg) -> {
            try {
                CacheInvalidateCommand cmd = objectMapper.readValue(msg, CacheInvalidateCommand.class);
                KvCacheAgentRemovePort port = (KvCacheAgentRemovePort) agentPortRegistry.get(
                        cmd.getCacheAgentName(), BehaviorType.REMOVE);
                port.removeLocal(cmd.getKey());
            } catch (Exception e) {
                log.error("[YetCache]Invalid message: {}", msg, e);
            }
        });
    }
}



















