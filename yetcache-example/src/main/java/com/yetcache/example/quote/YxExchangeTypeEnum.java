package com.yetcache.example.quote;


import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author limingcheng
 * @Description 友信交易类别
 * @Date 2018-12-1 10:54
 * @Version v1.0
 **/
@Slf4j
public enum YxExchangeTypeEnum {
    HK(0, "HK", "港股", CapitalMoneyTypeEnum.HKD, ZoneId.of("Asia/Shanghai"), "hk", Boolean.TRUE),
    HGT(6, "HGT", "沪港通", CapitalMoneyTypeEnum.CNH, ZoneId.of("Asia/Shanghai"), "sh", Boolean.TRUE),
    SGT(7, "SGT", "深港通", CapitalMoneyTypeEnum.CNH, ZoneId.of("Asia/Shanghai"), "sz", Boolean.TRUE),
    SH_A(1, "SH-A", "上海A", CapitalMoneyTypeEnum.CNH, ZoneId.of("Asia/Shanghai"), Boolean.TRUE),
    SH_B(2, "SH-B", "上海B", CapitalMoneyTypeEnum.CNH, ZoneId.of("Asia/Shanghai"), Boolean.TRUE),
    SZ_A(3, "SZ-A", "深圳A", CapitalMoneyTypeEnum.CNH, ZoneId.of("Asia/Shanghai"), Boolean.TRUE),
    SZ_B(4, "SZ-B", "深圳B", CapitalMoneyTypeEnum.CNH, ZoneId.of("Asia/Shanghai"), Boolean.TRUE),

    USA(5, "US", "美股", CapitalMoneyTypeEnum.USD, ZoneId.of("America/New_York"), "us", Boolean.TRUE),
    /**
     * us_option-美股期权
     */
    USA_OPTION(51, "us_option", "美股期权", CapitalMoneyTypeEnum.USD, ZoneId.of("America/New_York"), "usoption", Boolean.TRUE),

    /**
     * 美股碎股按 us_option-美股期权交易日 走，清算时先清算碎股
     */
    USA_FRACTIONAL(52, "us_option", "美股碎股", CapitalMoneyTypeEnum.USD, ZoneId.of("America/New_York"), "us"),


    //用于查询不能用于业务处理，业务处理用具体的市场类型
    A(67, "A", "A股通", CapitalMoneyTypeEnum.CNH, ZoneId.of("Asia/Shanghai"), "a"),
    HK_USA(50, "港美", "港美"),

    ALL(100, "ALL", "所有市场");

    private Integer value;
    private String msg;
    private String chinessMsg;

    private CapitalMoneyTypeEnum enumMoneyType;
    /**
     * 市场时区
     */
    private ZoneId zoneId;
    //用于查询行情价格的参数:market
    private String market;
    /**
     * 当前市场是否启用
     */
    private Boolean validStatus;

    YxExchangeTypeEnum(Integer value, String msg, String chinessMsg) {
        this.value = value;
        this.msg = msg;
        this.chinessMsg = chinessMsg;
    }

    YxExchangeTypeEnum(Integer value, String msg, String chinessMsg, CapitalMoneyTypeEnum enumMoneyType,
                       ZoneId zoneId, String market) {
        this.value = value;
        this.msg = msg;
        this.chinessMsg = chinessMsg;
        this.enumMoneyType = enumMoneyType;
        this.zoneId = zoneId;
        this.market = market;
    }

    YxExchangeTypeEnum(Integer value, String msg, String chinessMsg, CapitalMoneyTypeEnum enumMoneyType,
                       ZoneId zoneId, Boolean validStatus) {
        this.value = value;
        this.msg = msg;
        this.chinessMsg = chinessMsg;
        this.enumMoneyType = enumMoneyType;
        this.zoneId = zoneId;
        this.validStatus = validStatus;
    }

    YxExchangeTypeEnum(Integer value, String msg, String chinessMsg, CapitalMoneyTypeEnum enumMoneyType,
                       ZoneId zoneId, String market, Boolean validStatus) {
        this.value = value;
        this.msg = msg;
        this.chinessMsg = chinessMsg;
        this.enumMoneyType = enumMoneyType;
        this.zoneId = zoneId;
        this.market = market;
        this.validStatus = validStatus;
    }


    public static YxExchangeTypeEnum getByValue(Integer value) {
        for (YxExchangeTypeEnum valueEnum : YxExchangeTypeEnum.values()) {
            if (Objects.equals(valueEnum.getValue(), value)) {
                return valueEnum;
            }
        }
        return null;
    }


    public Integer getValue() {
        return value;
    }

    public String getMsg() {
        return msg;
    }

    public String getChinessMsg() {
        return chinessMsg;
    }

    public CapitalMoneyTypeEnum getEnumMoneyType() {
        return enumMoneyType;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public String getMarket() {
        return market;
    }

    public Boolean getValidStatus() {
        return validStatus;
    }

}
