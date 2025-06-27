package com.yetcache.core.config;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author walter.yan
 * @since 2025/6/26
 */
@Data
@ConfigurationProperties(prefix = "yetcache")
@Component
@Slf4j
public class YetCacheProperties {
    private GlobalConfig global = new GlobalConfig();
    protected CacheGroups caches = new CacheGroups();

    @PostConstruct
    public void init() {
        log.info("YetCacheProperties init: " + JSON.toJSONString(this));
    }
}
