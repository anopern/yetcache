package com.yetcache.example.quote;

import lombok.extern.slf4j.Slf4j;


/**
 * @author youxin
 * @Description:金额的币种类型 CNY人民币;USD美元;HKD港币;
 * @date 2019年9月9日
 */
@Slf4j
public enum CapitalMoneyTypeEnum  {
    /**
     * `人民币
     */
    CNH(0, "人民币", "CNH", "人民幣"),

    /**
     * `美元
     */
    USD(1, "美元", "USD", "美元"),

    /**
     * `港币
     */
    HKD(2, "港币", "HKD", "港幣"),

    ;

    private Integer key;

    private String value;

    private String msgEn;

    private String msgTw;

    CapitalMoneyTypeEnum(int key, String value, String msgEn, String msgTw) {
        this.key = key;
        this.value = value;
        this.msgEn = msgEn;
        this.msgTw = msgTw;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getMsgEn() {
        return msgEn;
    }

    public String getMsgTw() {
        return msgTw;
    }
}
