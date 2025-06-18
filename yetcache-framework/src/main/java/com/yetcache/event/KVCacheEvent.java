package com.yetcache.event;

import com.yetcache.enums.KVCacheEventType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class KVCacheEvent<E> extends CacheEvent<E> {
    protected String bizKey;
    protected KVCacheEventType eventType;

    public static <E> CacheEvent<E> buildUpdateEvent(@NotNull String agentId,
                                                       @Nullable Long tenantId,
                                                       @NotNull String bizKey,
                                                       @NotNull E data) {
        KVCacheEvent<E> event = new KVCacheEvent<>();
        event.setAgentId(agentId);
        event.setTenantId(tenantId);
        event.setBizKey(bizKey);
        event.setEventType(KVCacheEventType.UPDATE);
        event.setData(data);
        return event;
    }

    public static <E> CacheEvent<E> buildInvalidateEvent(@NotNull String agentId,
                                                           @Nullable Long tenantId,
                                                           @NotNull String bizKey) {
        KVCacheEvent<E> event = new KVCacheEvent<>();
        event.setAgentId(agentId);
        event.setTenantId(tenantId);
        event.setEventType(KVCacheEventType.INVALIDATE);
        event.setBizKey(bizKey);
        return event;
    }
}
