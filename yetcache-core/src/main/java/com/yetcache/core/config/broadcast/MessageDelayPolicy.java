package com.yetcache.core.config.broadcast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

/**
 * @author walter.yan
 * @since 2025/8/16
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageDelayPolicy {
    private Duration maxAge;
    private ExceededAction action;

    public static MessageDelayPolicy ofSeconds(long secs, ExceededAction action) {
        return new MessageDelayPolicy(Duration.ofSeconds(secs), action);
    }

    public Decision decide(Long publishedAt) {
        if (publishedAt == null) {
            return Decision.APPLY_WITH_WARN;
        }
        Duration age = Duration.between(Instant.ofEpochMilli(publishedAt),
                Instant.ofEpochMilli(System.currentTimeMillis()));
        if (!age.isNegative() && age.compareTo(maxAge) <= 0) {
            return Decision.APPLY;
        }

        if (action == ExceededAction.DROP) {
            return Decision.DROP;
        } else if (action == ExceededAction.REMOVE) {
            return Decision.REMOVE;
        } else if (action == ExceededAction.REFRESH) {
            return Decision.REFRESH;
        } else {
            return Decision.APPLY_WITH_WARN;
        }
    }
}
