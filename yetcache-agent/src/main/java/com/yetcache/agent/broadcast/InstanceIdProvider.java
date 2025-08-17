package com.yetcache.agent.broadcast;

import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.Optional;

/**
 * @author walter.yan
 * @since 2025/7/16
 */
@Slf4j
public class InstanceIdProvider {
    private static volatile String instanceId;

    public static String getInstanceId() {
        if (instanceId != null) {
            return instanceId;
        }

        synchronized (InstanceIdProvider.class) {
            if (instanceId != null) {
                return instanceId;
            }

            String appName = Optional.ofNullable(System.getProperty("spring.application.name")).orElse("yetcache");
            String ip = resolveLocalIp().replace(".", "_");
            int pidHash = ManagementFactory.getRuntimeMXBean().getName().hashCode();

            instanceId = String.format("%s-%s-%d", appName, ip, pidHash);
            log.info("[YetCache] Auto-generated instanceId: {}", instanceId);
            return instanceId;
        }
    }

    private static String resolveLocalIp() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress();
        } catch (Exception e) {
            return "unknown_ip";
        }
    }
}
