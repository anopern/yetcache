package lab.anoper.yetcache.utils;

import java.util.function.Supplier;

/**
 * @author walter.yan
 * @since 2025/5/22
 */
public class CacheRetryUtils {
    /**
     * 重试获取 Redis 缓存数据
     *
     * @param supplier 获取数据的函数（例如 Redis 查询）
     * @param maxAttempts 最大尝试次数（含首次）
     * @param sleepMillis 每次失败后的等待间隔（毫秒）
     * @param <T> 返回的数据类型
     * @return 若某次重试成功，则返回对应数据；若所有尝试均失败，则返回 null
     */
    public static <T> T retryRedisGet(Supplier<T> supplier, int maxAttempts, long sleepMillis) {
        for (int i = 0; i < maxAttempts; i++) {
            try {
                T result = supplier.get();
                if (result != null) {
                    return result;
                }
                if (i < maxAttempts - 1) {
                    Thread.sleep(sleepMillis);
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Redis自旋重试被中断", ie);
            } catch (Exception e) {
                // 捕获 Redis 或 Lambda 中的异常，日志由调用方决定是否记录
            }
        }
        return null;
    }
}
