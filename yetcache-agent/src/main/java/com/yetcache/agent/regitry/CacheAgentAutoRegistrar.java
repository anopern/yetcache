//package com.yetcache.agent.regitry;
//
//import com.yetcache.agent.BaseKVCacheAgent;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationListener;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
///**
// * @author walter.yan
// * @since 2025/7/2
// */
//@Component
//public class CacheAgentAutoRegistrar implements ApplicationListener<ApplicationReadyEvent> {
//    @Autowired
//    private ApplicationContext applicationContext;
//    @Autowired
//    private CacheAgentRegistry registry;
//
//    @Override
//    public void onApplicationEvent(ApplicationReadyEvent event) {
//        Map<String, BaseKVCacheAgent> kvAgentBeans = applicationContext.getBeansOfType(BaseKVCacheAgent.class);
//        for (BaseKVCacheAgent<?, ?> agent : kvAgentBeans.values()) {
//            registry.register(agent);
//        }
//    }
//}