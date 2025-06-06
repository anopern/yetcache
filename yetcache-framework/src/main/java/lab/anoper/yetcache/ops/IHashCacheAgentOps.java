//package lab.anoper.yetcache.ops;
//
//import com.yxzq.common.cache.domain.dto.CacheAgentOpsRequest;
//
//import javax.validation.constraints.NotNull;
//
///**
// * @author walter.yan
// * @since 2025/5/25
// * <p>
// * Hash 类型缓存 Agent 的统一运维接口。
// * 支持基于 Agent 名称的统一操作调用（适用于运维平台或调度中心如 XXL-Job）。
// * 主要面向运维/调度/调试用途，允许精细化操作 Hash 缓存的局部或全量数据。
// */
//public interface IHashCacheAgentOps {
//    String SEPARATOR = "\r\n";
//
////    String view(Long tenantId, String bizKey, String... bizHashKeys);
//
//    String viewAll(@NotNull CacheAgentOpsRequest req);
//
////    void refreshJvm(Long tenantId, String bizKey, String... bizHashKeys);
////
////    void refreshRedis(Long tenantId, String bizKey, String... bizHashKeys);
//
//    void refreshAll(@NotNull CacheAgentOpsRequest req);
//
////    void evict(Long tenantId, String bizKey, String... bizHashKeys);
//
//    void evictAll(@NotNull CacheAgentOpsRequest req);
//}
//
