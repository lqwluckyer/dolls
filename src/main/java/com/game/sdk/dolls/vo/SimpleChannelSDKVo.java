package com.game.sdk.dolls.vo;

import java.io.Serializable;

public class SimpleChannelSDKVo implements Serializable {
    private static final long serialVersionUID = 2773321497797163826L;
    private Integer id;
    private String sdkName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSdkName() {
        return sdkName;
    }

    public void setSdkName(String sdkName) {
        this.sdkName = sdkName;
    }
}
