package com.yetcache.starter;

import com.yetcache.agent.broadcast.subscriber.CacheInvalidateMessageSubscriber;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author walter.yan
 * @since 2025/8/31
 */
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class YetcacheSubscriberBootstrap  implements ApplicationListener<ApplicationReadyEvent> {
    private final CacheInvalidateMessageSubscriber subscriber;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        subscriber.subscribe();
    }
}
