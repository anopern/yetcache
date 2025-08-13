package com.yetcache.agent.broadcast.receiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetcache.agent.broadcast.InstanceIdProvider;
import com.yetcache.agent.broadcast.command.CacheUpdateCommand;
import com.yetcache.agent.broadcast.receiver.handler.CacheBroadcastHandler;
import com.yetcache.agent.broadcast.receiver.handler.CacheBroadcastHandlerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.Optional;

/**
 * @author walter.yan
 * @since 2025/7/27
 */
@Slf4j
@RequiredArgsConstructor
public class RabbitMqCacheBroadcastReceiver implements CacheBroadcastReceiver {
    private final ObjectMapper objectMapper;
    private final CacheBroadcastHandlerRegistry handlerRegistry;

    @RabbitListener(queues = "#{broadcastQueueName}", concurrency = "1-3")
    @Override
    public void onMessage(String messageJson) {
        try {
            log.debug("receive message: {}", messageJson);
            CacheUpdateCommand cmd = objectMapper.readValue(messageJson, CacheUpdateCommand.class);
            if (InstanceIdProvider.getInstanceId().equalsIgnoreCase(cmd.getDescriptor().getInstanceId())) {
                log.debug("ignore local published message: {}", messageJson);
                return;
            }
            Optional<CacheBroadcastHandler> handlerOpt = handlerRegistry.getHandler(cmd);
            if (handlerOpt.isEmpty()) {
                log.error("[YetCache] No broadcast handler for: {}", messageJson);
                return;
            }
            handlerOpt.get().handle(cmd);
        } catch (Exception e) {
            log.warn("[YetCache] Failed to process broadcast message: {}", messageJson, e);
        }
    }
}