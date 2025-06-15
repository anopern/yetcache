package lab.anoper.yetcache.agent.impl;


import cn.hutool.core.util.StrUtil;
import lab.anoper.yetcache.properties.BaseCacheAgentProperties;

import javax.validation.constraints.NotNull;

/**
 * @author walter.yan
 * @since 2025/5/23
 */
public abstract class AbstractMultiKeyCacheAgent<E> extends AbstractCacheAgent<E> {
    public AbstractMultiKeyCacheAgent(BaseCacheAgentProperties properties) {
        super(properties);
    }

    protected abstract String getBizKey(@NotNull E e);

    protected String getQuerySourceDistLockKey(Long tenantId, String bizKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s:%s", QUERY_SOURCE_LOCK_KEY_PRE, getAgentId()));
        if (tenantId != null) {
            sb.append(String.format(":%d:%s", tenantId, bizKey));
        } else {
            sb.append(String.format(":%s", bizKey));
        }
        return sb.toString();
    }


    protected String getKeyFromBizKeyWithTenant(Long tenantId, String bizKey) {
        if (null == tenantId) {
            return String.format("%s:%s", properties.getCacheKey(), bizKey);
        }
        return String.format("%s:%d:%s", properties.getCacheKey(), tenantId, bizKey);
    }
}
