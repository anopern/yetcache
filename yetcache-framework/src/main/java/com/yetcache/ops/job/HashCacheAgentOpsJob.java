//package lab.anoper.yetcache.ops.job;
//
//import com.alibaba.fastjson.JSON;
//import com.xxl.job.core.biz.model.ReturnT;
//import com.xxl.job.core.handler.annotation.XxlJob;
//import com.xxl.job.core.log.XxlJobLogger;
//import com.yxzq.common.cache.agent.impl.AbstractCacheAgent;
//import com.yxzq.common.cache.bootstrap.CacheAgentRegistry;
//import com.yxzq.common.cache.domain.dto.CacheAgentOpsRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * @author walter.yan
// * @since 2025/5/25
// */
//@Component
//@Slf4j
//public class HashCacheAgentOpsJob {
//    @Autowired
//    private CacheAgentRegistry cacheAgentRegistry;
//
//    @XxlJob("cacheAgentOps")
//    public ReturnT<String> cacheAgentOps(String param) {
//        CacheAgentOpsRequest req = parseRequest(param);
//        assert null != req;
//        AbstractCacheAgent<?> agent = cacheAgentRegistry.getByName(req.getAgentName());
//
//        return ReturnT.SUCCESS;
//    }
//
//    private CacheAgentOpsRequest parseRequest(String param) {
//        try {
//            return JSON.parseObject(param, CacheAgentOpsRequest.class);
//        } catch (Exception e) {
//            log.error("参数解析失败，参数：{}", param, e);
//            XxlJobLogger.log("参数解析失败", e.getMessage());
//        }
//        return null;
//    }
//}
