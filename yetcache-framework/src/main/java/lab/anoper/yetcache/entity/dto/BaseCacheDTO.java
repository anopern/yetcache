package lab.anoper.yetcache.entity.dto;

import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/6/16
 */
@Data
public class BaseCacheDTO {
    /**
     * true 表示这是一个占位符对象（用于穿透保护）
     */
    protected Boolean placeholder;

    /**
     * 版本号：可用于跨实例更新控制、消息刷新版本对比等
     */
    protected Long version;

    public boolean isPlaceholder() {
        return Boolean.TRUE.equals(placeholder);
    }

    public boolean isNotPlaceholder() {
        return null == placeholder || Boolean.FALSE.equals(placeholder);
    }

    public void markAsPlaceholder() {
        this.placeholder = true;
        this.version = System.currentTimeMillis(); // 占位时自动打上版本戳，便于广播比对
    }

    public boolean isNewerThan(BaseCacheDTO other) {
        if (other == null || other.version == null) return true;
        return this.version != null && this.version > other.version;
    }

    public static <T extends BaseCacheDTO> T getPlaceholder(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            instance.setPlaceholder(true);
            instance.setVersion(System.currentTimeMillis());
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("无法创建占位对象: " + clazz.getName(), e);
        }
    }

    @Override
    public String toString() {
        return String.format("DTO[placeholder=%s, version=%s]", placeholder, version);
    }
}
