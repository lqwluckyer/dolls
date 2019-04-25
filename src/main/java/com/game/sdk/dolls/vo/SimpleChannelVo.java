package com.game.sdk.dolls.vo;

import java.io.Serializable;

public class SimpleChannelVo implements Serializable {
    private static final long serialVersionUID = 483279751906548253L;
    private Integer id;

    //渠道名称
    private String channelName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
