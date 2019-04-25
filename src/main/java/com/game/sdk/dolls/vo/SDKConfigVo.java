package com.game.sdk.dolls.vo;

import java.io.Serializable;

public class SDKConfigVo implements Serializable {

    private static final long serialVersionUID = 7364820656518225203L;
    /**
     * 1 表示从 channel 配置中取值
     * 2 表示从 请求参数 中取值
     * 3 表示该参数是 sign
     */
    private Integer type;

    /**
     *  签名拼接值或请求参数名
     */
    private String key;

    /**
     *  获取签名拼接值或请求参数名对应的值的KEY
     */
    private String value;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
