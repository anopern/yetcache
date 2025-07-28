package com.yetcache.agent.broadcast.receiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetcache.agent.broadcast.command.ExecutableCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * @author walter.yan
 * @since 2025/7/27
 */
@Slf4j
@RequiredArgsConstructor
public class RabbitMqCacheBroadcastReceiver implements CacheBroadcastReceiver {
    private final ObjectMapper objectMapper;
    private final ExecutableCommandDispatcher dispatcher;

    @RabbitListener(queues = "#{broadcastQueueName}", concurrency = "1-3")
    @Override
    public void onMessage(String messageJson) {
        try {
            ExecutableCommand cmd = objectMapper.readValue(messageJson, ExecutableCommand.class);
            dispatcher.onReceive(cmd);
        } catch (Exception e) {
            log.warn("[YetCache] Failed to process broadcast message: {}", messageJson, e);
        }
    }
}