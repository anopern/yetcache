package lab.anoper.yetcache.mq.event;

import lab.anoper.yetcache.enums.CacheEventType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

/**
 * @author walter.yan
 * @since 2025/5/24
 */

@Getter
@Setter
public class CacheEvent<E> implements ResolvableTypeProvider {
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
                                                       @NotNull E data) {
        CacheEvent<E> event = new CacheEvent<>();
        event.setAgentId(agentId);
        event.setTenantId(tenantId);
        event.setEventType(CacheEventType.UPDATE);
        event.setData(data);
        return event;
    }

    public static <E> CacheEvent<E> buildKVInvalidateEvent(@NotNull String agentId, @Nullable Long tenantId,
                                                           @NotNull String bizKey) {
        CacheEvent<E> event = new CacheEvent<>();
        event.setAgentId(agentId);
        event.setTenantId(tenantId);
        event.setEventType(CacheEventType.INVALIDATE);
        event.setBizKey(new CacheEvent.BizKey(bizKey));
        return event;
    }
}

