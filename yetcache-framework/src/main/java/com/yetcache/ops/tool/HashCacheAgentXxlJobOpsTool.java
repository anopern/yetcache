//package lab.anoper.yetcache.ops.tool;
//
//import com.xxl.job.core.log.XxlJobLogger;
//import com.yxzq.common.cache.bootstrap.CacheAgentRegistry;
//import com.yxzq.common.cache.domain.dto.CacheAgentOpsRequest;
//import com.yxzq.common.cache.ops.IHashCacheAgentOps;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.validation.constraints.NotNull;
//
///**
// * @author walter.yan
// * @since 2025/5/25
// */
//@Component
//public class HashCacheAgentXxlJobOpsTool {
//    @Autowired
//    private CacheAgentRegistry agentRegistry;
//
//    public String viewAll(@NotNull CacheAgentOpsRequest req) {
//        IHashCacheAgentOps agent = agentRegistry.getHashCacheAgentByName(req.getAgentName());
//        if (agent == null) {
//            XxlJobLogger.log("HashCacheAgent [{}] 没有找到", req.getAgentName());
//            return "Agent not found: " + req.getAgentName();
//        }
//        return agent.viewAll(req);
//    }
//
//    public void refreshAll(@NotNull CacheAgentOpsRequest req) {
//        IHashCacheAgentOps agent = agentRegistry.getHashCacheAgentByName(req.getAgentName());
//        if (agent == null) {
//            XxlJobLogger.log("HashCacheAgent [{}] 没有找到", req.getAgentName());
//            return;
//        }
//        agent.refreshAll(req);
//    }
//
//    public void evictAll(@NotNull CacheAgentOpsRequest req) {
//        IHashCacheAgentOps agent = agentRegistry.getHashCacheAgentByName(req.getAgentName());
//        if (agent == null) {
//            XxlJobLogger.log("HashCacheAgent [{}] 没有找到", req.getAgentName());
//            return;
//        }
//        agent.evictAll(req);
//    }
//}
