package com.yetcache.agent.broadcast;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.yetcache.core.config.broadcast.RabbitMqConfig;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
public class BroadcastQueueInitializer {

    public static String init(Channel channel, RabbitMqConfig config) throws IOException {
        // 1. 声明 fanout exchange（幂等）
        String exchange = config.getExchange();
        channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT, true);

        // 2. 构建 queue 名称
        String queueName;
        if (Boolean.TRUE.equals(config.getAnonymous())) {
            queueName = ""; // broker 会自动生成
        } else {
            String instanceId = InstanceIdProvider.getInstanceId();
            queueName = config.getQueuePrefix() + instanceId;
        }

        // 3. 构建 queue 参数
        Map<String, Object> args = new HashMap<>();
        if (config.getQueueExpireMs() != null) {
            args.put("x-expires", config.getQueueExpireMs());
        }

        // 4. 声明 queue（自动处理匿名/命名）
        String actualQueueName = channel.queueDeclare(
                queueName,
                defaultTrue(config.getDurable()),
                false,
                defaultFalse(config.getAutoDelete()),
                args
        ).getQueue();

        // 5. 绑定到 exchange
        channel.queueBind(actualQueueName, exchange, "");

        System.out.println("[YetCache] Broadcast queue declared and bound: " + actualQueueName);

        return actualQueueName;
    }

    private static boolean defaultTrue(Boolean value) {
        return value == null || value;
    }

    private static boolean defaultFalse(Boolean value) {
        return value != null && value;
    }

}
