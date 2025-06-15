package lab.anoper.yetcache.event;

import lab.anoper.yetcache.enums.MultiHashCacheEventType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class MultiHashCacheEvent<E> extends CacheEvent<E> {
    protected MultiHashCacheEventType eventType;
    protected String bizKey;
    protected String bizHashKey;

    public static <E> MultiHashCacheEvent<Map<String, E>> buildReplaceAllEvent(@NotNull String agentId,
                                                                               @Nullable Long tenantId,
                                                                               @NotNull String bizKey,
                                                                               @NotNull Map<String, E> dataMap) {
        MultiHashCacheEvent<Map<String, E>> event = new MultiHashCacheEvent<>();
        event.setAgentId(agentId);
        event.setTenantId(tenantId);
        event.setEventType(MultiHashCacheEventType.UPDATE_HASH);
        event.setBizKey(bizKey);
        event.setData(dataMap);
        return event;
    }

    public static <Void> MultiHashCacheEvent<Void> buildInvalidateHashEvent(@NotNull String agentId,
                                                                            @Nullable Long tenantId,
                                                                            @NotNull String bizKey) {
        MultiHashCacheEvent<Void> event = new MultiHashCacheEvent<>();
        event.setAgentId(agentId);
        event.setTenantId(tenantId);
        event.setEventType(MultiHashCacheEventType.INVALIDATE_HASH);
        event.setBizKey(bizKey);
        return event;
    }


    public static <E> MultiHashCacheEvent<E> buildUpdateEntryEvent(@NotNull String agentId,
                                                                   @Nullable Long tenantId,
                                                                   @NotNull String bizKey,
                                                                   @NotNull String bizHashKey,
                                                                   @NotNull E e) {
        MultiHashCacheEvent<E> event = new MultiHashCacheEvent<>();
        event.setAgentId(agentId);
        event.setTenantId(tenantId);
        event.setEventType(MultiHashCacheEventType.UPDATE_ENTRY);
        event.setBizKey(bizKey);
        event.setBizHashKey(bizHashKey);
        event.setData(e);
        return event;
    }

}
