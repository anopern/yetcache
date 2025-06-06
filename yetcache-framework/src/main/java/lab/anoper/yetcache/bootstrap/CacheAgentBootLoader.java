//package lab.anoper.yetcache.bootstrap;
//
//import com.yxzq.common.cache.agent.HotDataReloadable;
//import com.yxzq.common.cache.agent.impl.AbstractCacheAgent;
//import com.yxzq.common.cache.hotkey.provider.IHotKeyProvider;
//import com.yxzq.common.cache.hotkey.provider.impl.AbstractHotKeyProvider;
//import com.yxzq.common.cache.tenant.ITenantIdProvider;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.ApplicationListener;
//import org.springframework.stereotype.Component;
//
//import java.util.Comparator;
//import java.util.List;
//import java.util.Set;
//
///**
// * @author walter.yan
// * @since 2025/5/23
// */
//@Component
//@Slf4j
//public class CacheAgentBootLoader implements ApplicationListener<ApplicationReadyEvent> {
//    @Autowired
//    private List<AbstractHotKeyProvider> hotKeyProviders;
//    @Autowired
//    private List<AbstractCacheAgent<?>> cacheAgents;
//    @Autowired
//    protected ITenantIdProvider tenantIdProvider;
//
//    @Override
//    public void onApplicationEvent(ApplicationReadyEvent event) {
//        try {
//            log.info("应用成功已经准备就绪，开始在JVM或者Redis预热缓存");
//            loadHotKeys();
//            loadHotData();
//        } catch (Exception e) {
//            log.error("加载失败！");
//        }
//    }
//
//    public void loadHotKeys() {
//        hotKeyProviders.sort(Comparator.comparingInt(AbstractHotKeyProvider::getOrder));
//        for (AbstractHotKeyProvider provider : hotKeyProviders) {
//            if (!provider.getProperties().isLoadOnStartup()) {
//                log.info("跳过加载热点Key：{}", provider.getClass().getSimpleName());
//                continue;
//            }
//            if (provider.isTenantScoped()) {
//                for (Long tenantId : tenantIdProvider.listAllTenantIds()) {
//                    loadHotKeys(provider, tenantId);
//                }
//            } else {
//                loadHotKeys(provider, null);
//            }
//        }
//    }
//
//    private void loadHotKeys(IHotKeyProvider provider, Long tenantId) {
//        long start = System.currentTimeMillis();
//        Set<String> bizKeys = provider.listAllBizKeys(tenantId);
//        long end = System.currentTimeMillis();
//        String durationSecs = String.format("%.3f", (end - start) / 1000f);
//        log.info("\r\n缓存热点Key加载完成，热点Key类型：{}，一共加载{}个热点Key，总共耗时{}s\r\n",
//                provider.getClass().getSimpleName(), bizKeys.size(), durationSecs);
//
//    }
//
//    public void loadHotData() {
//        long start = System.currentTimeMillis();
//        // 从小到大排序
//        cacheAgents.sort(Comparator.comparingInt(HotDataReloadable::getOrder));
//        for (AbstractCacheAgent<?> agent : cacheAgents) {
//            // 开机时，是否从数据源加载到Redis
//            if (agent.getProperties().isLoadFromSourceOnStartup()) {
//                agent.loadFromSource();
//            }
//            // 开机时，是偶从Redis加载到JVM
//            if (agent.getProperties().isLocalCacheEnabled()) {
//                agent.loadFromRedis();
//            }
//        }
//        long end = System.currentTimeMillis();
//        String durationSecs = String.format("%.3f", (end - start) / 1000f);
//        log.info("\r\n缓存Agent预热完成，一共处理{}个Agent，总共耗时{}s\r\n", cacheAgents.size(), durationSecs);
//    }
//}
