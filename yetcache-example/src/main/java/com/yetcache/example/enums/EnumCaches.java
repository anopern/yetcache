package com.yetcache.example.enums;

/**
 * @author walter.yan
 * @since 2025/7/1
 */
public enum EnumCaches {
    USER_ID_KEY_CACHE("user-id-key-cache", ""),
    CONFIG_COMMON_INFO_CACHE("config-common-info-cache", ""),
    STOCK_HOLD_INFO_CACHE("stock-hold-info-cache", "");;

    private final String name;
    private final String desc;

    EnumCaches(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

}
