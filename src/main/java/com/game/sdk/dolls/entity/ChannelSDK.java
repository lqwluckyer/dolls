package com.game.sdk.dolls.entity;

import javax.persistence.*;

/**
 * 渠道商SDK
 */
@Entity
@Table(name = "t_channel_sdk")
public class ChannelSDK extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //当前渠道商所使用的sdk名称
    private String sdkName;

    // 当前渠道商所使用的sdk简称
    private String sdkShortName;

    //当前SDK登录认证地址
    private String authUrl;

    //当前SDK支付通知回调地址
    private String payCallbackUrl;

    //当前SDK的验证处理类的全类名
    private String verifyClass;

    //SDK订单号获取地址
    private String orderUrl;

    // 其他配置 , JSON 格式
    @Column(length = 1024)
    private String otherConfig;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSdkShortName() {
        return sdkShortName;
    }

    public void setSdkShortName(String sdkShortName) {
        this.sdkShortName = sdkShortName;
    }

    public String getSdkName() {
        return sdkName;
    }

    public void setSdkName(String sdkName) {
        this.sdkName = sdkName;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getPayCallbackUrl() {
        return payCallbackUrl;
    }

    public void setPayCallbackUrl(String payCallbackUrl) {
        this.payCallbackUrl = payCallbackUrl;
    }

    public String getVerifyClass() {
        return verifyClass;
    }

    public void setVerifyClass(String verifyClass) {
        this.verifyClass = verifyClass;
    }

    public String getOrderUrl() {
        return orderUrl;
    }

    public void setOrderUrl(String orderUrl) {
        this.orderUrl = orderUrl;
    }

    public String getOtherConfig() {
        return otherConfig;
    }

    public void setOtherConfig(String otherConfig) {
        this.otherConfig = otherConfig;
    }
}
