package lab.anoper.yetcache.enums;

import lombok.Getter;

@Getter
public enum TenantCheckLevel {
    MUST("必须校验租户"),
    OPTIONAL( "可选校验租户"),
    NONE("不校验租户");

    private final String desc;

    TenantCheckLevel(String desc) {

        this.desc = desc;
    }
}