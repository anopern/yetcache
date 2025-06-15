package lab.anoper.yetcache.agent.impl;

import lab.anoper.yetcache.properties.BaseCacheAgentProperties;

public class AbstractSingleHashCacheAgent<E> extends AbstractCacheAgent<E>{
    public AbstractSingleHashCacheAgent(BaseCacheAgentProperties properties) {
        super(properties);
    }

    @Override
    public boolean isTenantScoped() {
        return false;
    }

    @Override
    protected String getBizKey(E e) {
        return null;
    }

    @Override
    public void handleMessage(String message) {

    }
}
