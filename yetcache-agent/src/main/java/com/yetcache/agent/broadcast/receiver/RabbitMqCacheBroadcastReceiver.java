package com.yetcache.agent.broadcast.receiver;

import cn.hutool.core.util.StrUtil;
import com.yetcache.agent.broadcast.InstanceIdProvider;
import com.yetcache.agent.broadcast.command.CacheShape;
import com.yetcache.agent.broadcast.command.CacheUpdateCommand;
import com.yetcache.agent.broadcast.command.CommandDescriptor;
import com.yetcache.agent.broadcast.command.playload.HashPlayload;
import com.yetcache.agent.broadcast.receiver.handler.CacheBroadcastHandler;
import com.yetcache.agent.broadcast.receiver.handler.CacheBroadcastHandlerRegistry;
import com.yetcache.core.codec.*;
import com.yetcache.core.config.broadcast.Decision;
import com.yetcache.core.config.broadcast.MessageDelayPolicy;
import com.yetcache.core.config.broadcast.MessageDelayPolicyRegistry;
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
    private final JsonTypeConverter jsonTypeConverter;
    private final CacheBroadcastHandlerRegistry handlerRegistry;
    private final MessageDelayPolicyRegistry delayPolicyRegistry;

    @RabbitListener(queues = "#{broadcastQueueName}", concurrency = "1-3")
    @Override
    public void onMessage(String messageJson) {
        try {
            log.debug("receive message: {}", messageJson);
            if (StrUtil.isBlank(messageJson)) {
                log.error("receive message is empty");
                return;
            }

            CacheUpdateCommand cmd = jsonValueCodec.decode(messageJson, CacheUpdateCommand.class);
            if (null == cmd || null == cmd.getDescriptor()) {
                log.warn("[YetCache] Invalid message: {}", messageJson);
                return;
            }

            CommandDescriptor descriptor = cmd.getDescriptor();
            if (InstanceIdProvider.getInstanceId().equalsIgnoreCase(descriptor.getInstanceId())) {
                log.debug("ignore local published message: {}", messageJson);
                return;
            }

            Optional<CacheShape> cacheShapeOpt = CacheShape.fromName(descriptor.getShape());
            if (!cacheShapeOpt.isPresent()) {
                log.error("[YetCache] No cache shape for: {}", descriptor.getShape());
                return;
            }

            MessageDelayPolicy delayPolicy = delayPolicyRegistry.get(descriptor.getComponentName());
            Decision decision = delayPolicy.decide(descriptor.getPublishAt());
            if (decision == Decision.DROP) {
                log.debug("ignore late published message: {}", messageJson);
                return;
            } else if (decision == Decision.REMOVE) {
                log.debug("remove stale entry: {}", messageJson);
            } else if (decision == Decision.REFRESH) {
                log.debug("[YetCache] refresh message: {}", messageJson);
            } else if (decision == Decision.APPLY) {
                if (cacheShapeOpt.get() == CacheShape.HASH) {
                    HashPlayload hashPlayload = jsonTypeConverter.convert(cmd.getPayload(), HashPlayload.class);
                    cmd.setPayload(hashPlayload);
                }
                Optional<CacheBroadcastHandler> handlerOpt = handlerRegistry.getHandler(descriptor.getStructureBehaviorKey());
                if (!handlerOpt.isPresent()) {
                    log.error("[YetCache] No broadcast handler for: {}", messageJson);
                    return;
                }
                handlerOpt.get().handle(cmd);
            }
        } catch (Exception e) {
            log.warn("[YetCache] Failed to process broadcast message: {}", messageJson, e);
        }
    }
}