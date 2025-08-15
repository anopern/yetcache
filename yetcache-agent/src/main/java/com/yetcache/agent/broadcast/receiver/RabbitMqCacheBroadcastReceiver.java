package com.yetcache.agent.broadcast.receiver;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetcache.agent.broadcast.InstanceIdProvider;
import com.yetcache.agent.broadcast.command.CacheUpdateCommand;
import com.yetcache.agent.broadcast.command.CacheUpdateCommandCodecJson;
import com.yetcache.agent.broadcast.command.CommandDescriptor;
import com.yetcache.agent.broadcast.command.CommandEnvelope;
import com.yetcache.agent.broadcast.receiver.handler.CacheBroadcastHandler;
import com.yetcache.agent.broadcast.receiver.handler.CacheBroadcastHandlerRegistry;
import com.yetcache.core.cache.support.CacheValueHolder;
import com.yetcache.core.codec.JsonValueCodec;
import com.yetcache.core.codec.TypeRef;
import com.yetcache.core.codec.WrapperReifier;
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
    private final WrapperReifier<CacheUpdateCommand> reifier;
    private final ObjectMapper objectMapper;
    private final JsonValueCodec jsonValueCodec;
    private final CacheBroadcastHandlerRegistry handlerRegistry;

    @RabbitListener(queues = "#{broadcastQueueName}", concurrency = "1-3")
    @Override
    public void onMessage(String messageJson) {
        try {
            log.debug("receive message: {}", messageJson);
            if (StrUtil.isBlank(messageJson)) {
                log.error("receive message is empty");
                return;
            }

            CacheUpdateCommand rawCmd = (CacheUpdateCommand) jsonValueCodec.decode(messageJson, reifier.targetType());
            if (null == rawCmd || null == rawCmd.getDescriptor()) {
                log.warn("[YetCache] Invalid message: {}", messageJson);
                return;
            }

            CommandDescriptor descriptor = rawCmd.getDescriptor();
            if (InstanceIdProvider.getInstanceId().equalsIgnoreCase(descriptor.getInstanceId())) {
                log.debug("ignore local published message: {}", messageJson);
                return;
            }
            reifier.reify(rawCmd, commandTypeRef)

            Optional<CacheBroadcastHandler> handlerOpt = handlerRegistry.getHandler(descriptor.getStructureBehaviorKey());
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