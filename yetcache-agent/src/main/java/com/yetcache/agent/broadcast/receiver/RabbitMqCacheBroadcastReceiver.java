package com.yetcache.agent.broadcast.receiver;

import cn.hutool.core.util.StrUtil;
import com.yetcache.agent.broadcast.InstanceIdProvider;
import com.yetcache.agent.broadcast.CacheRemoveCommand;
import com.yetcache.agent.broadcast.receiver.handler.CacheBroadcastHandler;
import com.yetcache.agent.broadcast.receiver.handler.CacheBroadcastHandlerRegistry;
import com.yetcache.agent.core.StructureType;
import com.yetcache.core.codec.*;
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
    private final JsonValueCodec jsonValueCodec;
    private final CacheBroadcastHandlerRegistry handlerRegistry;

    @RabbitListener(queues = "#{broadcastQueueName}", concurrency = "1-3")
    @Override
    public void onMessage(String messageJson) {
        try {
            log.debug("[Yetcache]receive message: {}", messageJson);
            if (StrUtil.isBlank(messageJson)) {
                log.error("[Yetcache]receive message is empty");
                return;
            }

            CacheRemoveCommand cmd = jsonValueCodec.decode(messageJson, CacheRemoveCommand.class);
            if (null == cmd) {
                log.warn("[YetCache] Invalid message: {}", messageJson);
                return;
            }

            if (InstanceIdProvider.getInstanceId().equalsIgnoreCase(cmd.getInstanceId())) {
                log.debug("[Yetcache]ignore local published message: {}", messageJson);
                return;
            }

            StructureType structureType = StructureType.fromString(cmd.getStructureType());
            Optional<CacheBroadcastHandler> handlerOpt = handlerRegistry.getHandler(structureType);
            if (!handlerOpt.isPresent()) {
                log.error("[YetCache] No broadcast handler for: {}", messageJson);
                return;
            }
            handlerOpt.get().handle(cmd);
        } catch (Exception e) {
            log.warn("[YetCache] Failed to process broadcast message: {}", messageJson, e);
        }
    }
}