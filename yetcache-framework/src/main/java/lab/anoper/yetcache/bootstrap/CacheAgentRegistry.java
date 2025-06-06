package lab.anoper.yetcache.bootstrap;

import lab.anoper.yetcache.agent.impl.AbstractCacheAgent;
import lab.anoper.yetcache.agent.impl.AbstractKVCacheAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CacheAgentRegistry {
    @Autowired(required = false)
    private List<AbstractCacheAgent<?>> cacheAgents= new ArrayList<>();
//    @Autowired(required = false)
//    private List<AbstractHashCacheAgentV2<?>> hashCacheAgents = new ArrayList<>();
    @Autowired(required = false)
    private List<AbstractKVCacheAgent<?>> kvCacheAgents = new ArrayList<>();

    // 使用线程安全 Map，支持后续动态注册
    private final Map<String, AbstractCacheAgent<?>> registry = new ConcurrentHashMap<>();
//    private final Map<String, AbstractHashCacheAgentV2<?>> hashCacheAgentRegistry = new ConcurrentHashMap<>();
    private final Map<String, AbstractKVCacheAgent<?>> kvCacheAgentRegistry = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        if (cacheAgents != null) {
            registry.putAll(cacheAgents.stream()
                    .collect(Collectors.toMap(AbstractCacheAgent::getId, Function.identity())));
        }
        if (kvCacheAgents != null) {
            kvCacheAgentRegistry.putAll(kvCacheAgents.stream()
                    .collect(Collectors.toMap(AbstractCacheAgent::getId, Function.identity())));
        }
//        if (hashCacheAgents != null) {
//            hashCacheAgentRegistry.putAll(hashCacheAgents.stream()
//                    .collect(Collectors.toMap(AbstractCacheAgent::getName, Function.identity())));
//        }

        log.info("CacheAgentRegistry initialized:");
        log.info("  all cacheAgents: {}", registry.keySet());
        log.info("  kvCacheAgents: {}", kvCacheAgentRegistry.keySet());
//        log.info("  hashCacheAgents: {}", hashCacheAgentRegistry.keySet());
    }

    public AbstractCacheAgent<?> getById(String name) {
        AbstractCacheAgent<?> agent = registry.get(name);
        if (agent == null) {
            log.warn("No cacheAgent found for name: {}", name);
        }
        return agent;
    }

//    public AbstractHashCacheAgentV2<?> getHashCacheAgentByName(String name) {
//        AbstractHashCacheAgentV2<?> agent = hashCacheAgentRegistry.get(name);
//        if (agent == null) {
//            log.warn("No hashCacheAgent found for name: {}", name);
//        }
//        return agent;
//    }

    public AbstractKVCacheAgent<?> getKVCacheAgentByName(String name) {
        AbstractKVCacheAgent<?> agent = kvCacheAgentRegistry.get(name);
        if (agent == null) {
            log.warn("No kvCacheAgent found for name: {}", name);
        }
        return agent;
    }

    /**
     * 动态注册一个 CacheAgent
     */
    public void registerCacheAgent(AbstractCacheAgent<?> agent) {
        if (agent == null || agent.getId() == null) {
            throw new IllegalArgumentException("agent or agent.name cannot be null");
        }
        registry.put(agent.getId(), agent);
        log.info("Registered cacheAgent: {}", agent.getId());
    }

    /**
     * 动态注销一个 CacheAgent
     */
    public void unregisterCacheAgent(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        registry.remove(name);
        kvCacheAgentRegistry.remove(name);
//        hashCacheAgentRegistry.remove(name);
        log.info("Unregistered cacheAgent: {}", name);
    }
}
