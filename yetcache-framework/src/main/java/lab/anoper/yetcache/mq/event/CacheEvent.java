package lab.anoper.yetcache.mq.event;

import com.alibaba.fastjson2.annotation.JSONType;
import lab.anoper.yetcache.agent.impl.AbstractCacheAgent;
import lab.anoper.yetcache.enums.CacheEventType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author walter.yan
 * @since 2025/5/24
 */

@Getter
@Setter
@JSONType(ignores = {"resolvableType"})
public class CacheEvent<E> implements ResolvableTypeProvider {
    protected String instanceId = AbstractCacheAgent.INSTANCE_ID;

    // 要通知的Agent的ID
    protected String agentId;

    // 是什么事件，更新、无效化等等
    protected CacheEventType eventType;

    // 哪个租户的数据
    protected Long tenantId;

    // 要操作的具体的数据的业务key
    protected CacheEvent.BizKey bizKey;

    // 具体要更新的数据，针对更新操作时候
    protected E data;

    // 创建时间，毫秒时间戳
    protected Long createdTime = System.currentTimeMillis();

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClass(getClass())
                .as(CacheEvent.class);
    }

    @Getter
    @Setter
    public static class BizKey {
        protected String bizKey;
        protected String bizField;

        public BizKey(String bizKey) {
            this.bizKey = bizKey;
        }

        public BizKey(String bizKey, String bizField) {
            this.bizKey = bizKey;
            this.bizField = bizField;
        }
    }

    public static <E> CacheEvent<E> buildKVUpdateEvent(@NotNull String agentId,
                                                       @Nullable Long tenantId,
                                                       @NotNull String bizKey,
                                                       @NotNull E data) {
        CacheEvent<E> event = new CacheEvent<>();
        event.setAgentId(agentId);
        event.setTenantId(tenantId);
        event.setBizKey(new BizKey(bizKey));
        event.setEventType(CacheEventType.UPDATE);
        event.setData(data);
        return event;
    }

    public static <E> CacheEvent<E> buildKVInvalidateEvent(@NotNull String agentId,
                                                           @Nullable Long tenantId,
                                                           @NotNull String bizKey) {
        CacheEvent<E> event = new CacheEvent<>();
        event.setAgentId(agentId);
        event.setTenantId(tenantId);
        event.setEventType(CacheEventType.INVALIDATE);
        event.setBizKey(new CacheEvent.BizKey(bizKey));
        return event;
    }

    public static <E> CacheEvent<Map<String, E>> buildHashUpdateHashEvent(@NotNull String agentId,
                                                                          @Nullable Long tenantId,
                                                                          @NotNull String bizKey,
                                                                          @NotNull Map<String, E> dataMap) {
        CacheEvent<Map<String, E>> event = new CacheEvent<>();
        event.setAgentId(agentId);
        event.setTenantId(tenantId);
        event.setEventType(CacheEventType.UPDATE_ALL_FIELDS);
        event.setBizKey(new CacheEvent.BizKey(bizKey));
        event.setData(dataMap);
        return event;
    }

    public static <E> CacheEvent<Map<String, E>> buildHashInvalidateAllFieldsEvent(@NotNull String agentId,
                                                                                   @Nullable Long tenantId,
                                                                                   @NotNull String bizKey) {
        CacheEvent<Map<String, E>> event = new CacheEvent<>();
        event.setAgentId(agentId);
        event.setTenantId(tenantId);
        event.setEventType(CacheEventType.INVALIDATE_ALL_FIELDS);
        event.setBizKey(new CacheEvent.BizKey(bizKey));
        return event;
    }

    public static <E> CacheEvent<E> buildHashUpdateEntryEvent(@NotNull String agentId,
                                                                           @Nullable Long tenantId,
                                                                           @NotNull String bizKey,
                                                                           @NotNull String bizField,
                                                                           @NotNull E e) {
        CacheEvent<E> event = new CacheEvent<>();
        event.setAgentId(agentId);
        event.setTenantId(tenantId);
        event.setEventType(CacheEventType.UPDATE_ENTRY);
        event.setBizKey(new CacheEvent.BizKey(bizKey, bizField));
        event.setData(e);
        return event;
    }
}

