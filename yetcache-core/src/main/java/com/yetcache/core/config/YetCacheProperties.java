package com.yetcache.core.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
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
@Slf4j
public class YetCacheProperties {
    private GlobalConfig global = new GlobalConfig();
    protected CacheGroups caches = new CacheGroups();

    @PostConstruct
    public void init() {
        log.info("\r\nYetCacheProperties init:\r\n " + JSON.toJSONString(this,
                JSONWriter.Feature.PrettyFormat));
    }
}
