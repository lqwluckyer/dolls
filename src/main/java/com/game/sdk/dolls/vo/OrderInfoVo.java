package com.game.sdk.dolls.vo;

import java.io.Serializable;

public class OrderInfoVo implements Serializable {
    // 订单号
    private Long orderId;

    // 支付回调地址
    private String callback;

    // 其他扩展参数， JSON 格式
    private String extension;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
