package lab.anoper.yetcache.agent.impl;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.alibaba.fastjson2.JSON;
import lab.anoper.yetcache.agent.ICacheAgent;
import lab.anoper.yetcache.event.CacheEvent;
import lab.anoper.yetcache.properties.BaseCacheAgentProperties;
import lab.anoper.yetcache.utils.RabbitMQUtils;
import lab.anoper.yetcache.utils.SpringContextProvider;
import lab.anoper.yetcache.utils.TenantIdUtils;
import lombok.Getter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.validation.constraints.NotNull;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.UUID;

/**
 * @author walter.yan
 * @since 2025/5/23
 */
public abstract class AbstractCacheAgent<E> implements ICacheAgent, ResolvableTypeProvider {
    // 应用启动时生成唯一ID
    public static final String INSTANCE_ID = UUID.randomUUID().toString();

    // 查询source的分布式锁防止数据源重复请求
    protected static final String QUERY_SOURCE_LOCK_KEY_PRE = "common:cache:agent:source";

    // 空对象缓存的key
    protected final static String EMPTY_OBJ_HASH_KEY = "EMPTY";

    protected RedisTemplate<String, E> redisTemplate;

    @Autowired
    protected RedissonClient redissonClient;

    @Autowired
    protected SpringContextProvider springContextProvider;

    @Getter
    protected BaseCacheAgentProperties properties;

    public AbstractCacheAgent(BaseCacheAgentProperties properties) {
        this.properties = properties;

        if (!properties.isLocalCacheEnabled() && !properties.isRedisCacheEnabled()) {
            throw new IllegalArgumentException("缓存Agent初始化失败，本地缓存和Redis缓存，至少要开启一中种！");
        }
    }

    void init()  throws Exception{
        checkProperties();
        initRedisTemplate();
    }

    protected abstract String getBizKey(@NotNull E e);

    public abstract void handleMessage(String message);

    protected CacheEvent<?> parseRawCacheEvent(String message) {
        return JSON.parseObject(message, CacheEvent.class);
    }

    protected CacheEvent<E> parseCacheEvent(String message) {
        Type type = getCacheEventObjectType();
        return JSON.parseObject(message, type);
    }

    protected Type getCacheEventObjectType() {
        Class<?> entityClass = resolveGenericTypeArgument();
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{entityClass};
            }

            @Override
            public Type getRawType() {
                return CacheEvent.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }

    protected void initRedisTemplate() {
        Class<E> clazz = (Class<E>) getResolvableType().getGeneric(0).resolve();
        // clone一个新的 RedisTemplate 并设置序列化器（避免污染全局模板）
        RedisTemplate<String, E> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(clazz));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new FastJsonRedisSerializer<>(clazz));
        RedisConnectionFactory redisConnectionFactory = springContextProvider.getBean(RedisConnectionFactory.class);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        this.redisTemplate = redisTemplate;
    }

    protected Class<?> resolveGenericTypeArgument() {
        ResolvableType type = ResolvableType.forClass(this.getClass()).as(AbstractCacheAgent.class);
        return type.getGeneric(0).resolve();
    }

    protected String getQuerySourceDistLockKey(Long tenantId, String bizKey) {
        return String.format("%s:%s:%d:%s", QUERY_SOURCE_LOCK_KEY_PRE, getAgentId(), tenantId, bizKey);
    }

    /**
     * 暴露当前实例的 ResolvableType，
     * 让调用方可以拿到 T、E 这两个泛型的真实类型
     */
    @Override
    public ResolvableType getResolvableType() {
        // 拿到 subclass 对应的 ResolvableType，然后 as 成 AbstractConfigAgent，
        // 这样才能 .getGeneric(0) / .getGeneric(1) 拿到 T、E
        return ResolvableType.forClass(getClass())
                .as(AbstractCacheAgent.class);
    }

    protected void publishEvent(CacheEvent<?> event) {
        String message = JSON.toJSONString(event);
        RabbitMQUtils.sendFanoutMessage(message, properties.getMqExchange());
    }

    @Override
    public String getAgentId() {
        return properties.getAgentId();
    }

    protected void checkProperties() {
        if (!properties.isLocalCacheEnabled() && !properties.isRedisCacheEnabled()) {
            throw new IllegalArgumentException("本地缓存和Redis缓存至少要启用一个！");
        }
        if (StrUtil.isBlank(properties.getCacheKey())) {
            throw new IllegalArgumentException("keyPrefix不能为空！");
        }
        if (StrUtil.isBlank(properties.getMqExchange())) {
            throw new IllegalArgumentException("mqExchange不能为空！");
        }
        if (StrUtil.isBlank(properties.getAgentId())) {
            throw new IllegalArgumentException("name不能为空！");
        }
    }

    protected Long getCurTenantId() {
        return TenantIdUtils.getCurTenantId(properties.getTenantCheckLevel());
    }

    protected String getKeyFromBizKeyWithTenant(Long tenantId, String bizKey) {
        if (null == tenantId) {
            return String.format("%s:%s", properties.getCacheKey(), bizKey);
        }
        return String.format("%s:%d:%s", properties.getCacheKey(), tenantId, bizKey);
    }

    protected void checkTenantScoped(Long tenantId) {
        if (isTenantScoped() && tenantId == null) {
            throw new IllegalArgumentException("当前缓存为租户级，必须传入租户ID！");
        }
        if (!isTenantScoped() && tenantId != null) {
            throw new IllegalArgumentException("当前缓存非租户级，请不要传入租户ID！");
        }
    }

    protected E getEmptyObject() {
        ResolvableType type = getResolvableType().getGeneric(0); // 拿到 T 的类型
        Class<?> clazz = type.resolve();
        if (clazz == null) {
            throw new IllegalStateException("无法解析泛型类型 E");
        }
        try {
            @SuppressWarnings("unchecked")
            E instance = (E) clazz.getDeclaredConstructor().newInstance();
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("创建空对象失败: " + clazz, e);
        }
    }

    protected boolean isEventFromCurrentInstance(CacheEvent<?> event) {
        return null != event && INSTANCE_ID.equalsIgnoreCase(event.getInstanceId());
    }
}
