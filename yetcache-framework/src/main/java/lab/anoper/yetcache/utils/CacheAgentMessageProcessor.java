package lab.anoper.yetcache.utils;

import com.alibaba.fastjson2.JSON;
import io.micrometer.core.instrument.util.StringUtils;
import lab.anoper.yetcache.agent.impl.AbstractCacheAgent;
import lab.anoper.yetcache.bootstrap.CacheAgentRegistry;
import lab.anoper.yetcache.mq.event.CacheEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

/**
 * @author walter.yan
 * @since 2025/5/22
 */
@Slf4j
@Component
public class CacheAgentMessageProcessor {
    @Autowired
    private CacheAgentRegistry cacheAgentRegistry;

    public void processMessage(Message message) {
        byte[] body = message.getBody();
        String json = new String(body, StandardCharsets.UTF_8);
        if (StringUtils.isBlank(json)) {
            log.error("MQ消息体为空，跳过消息处理！消息：{}", JSON.toJSONString(message));
            return;
        }

        try {
            CacheEvent<?> rwaEvent = JSON.parseObject(json, CacheEvent.class);
            AbstractCacheAgent<?> agent = cacheAgentRegistry.getById(rwaEvent.getAgentId());
            agent.handleMessage(json);
        } catch (Exception e) {
            log.error("处理缓存Agent广播消息出现异常，消息：{}", JSON.toJSONString(message), e);
        }
    }
}
