package com.game.sdk.dolls.entity;
import javax.persistence.*;

/**
 * 渠道对象(每个游戏对某个渠道)
 */

@Entity
@Table(name = "t_channel")
public class Channel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //渠道名称
    private String channelName;

    //游戏ID t_game 表中的ID
    private Integer gameId;

    //渠道商ID  t_channel_sdk 表中的ID
    private Integer sdkId;

    //参数配置信息，json 格式
    @Column(length = 2048)
    private String parametersConfig;

    // 登陆配置 JSON
    @Column(length = 2048)
    private String loginReqConfig;

    // 登陆配置签名 JSON
    @Column(length = 2048)
    private String loginSignConfig;

    @Column(length = 2048)
    private String loginRespConfig;

    // 下单配置 JSON
    @Column(length = 1024)
    private String orderReqConfig;

    // 下单配置签名 JSON
    @Column(length = 1024)
    private String orderSignConfig;

    @Column(length = 1024)
    private String orderRespConfig;

    // 支付回调配置
    @Column(length = 1024)
    private String callbackConfig;

    // 支付回调签名配置
    @Column(length = 1024)
    private String callbackSignConfig;

    // SDK登陆处理 bean 名称
    private String loginHandingBean;

    // SDK下单处理 bean 名称
    private String orderHandingBean;

    //充值功能状态，true：开放；false：关闭
    private Boolean openPay;

    //  登陆功能   true：开放；false：关闭
    private Boolean openLogin;

    //  注册功能  true：开放；false：关闭
    private Boolean openRegister;

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

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Integer getSdkId() {
        return sdkId;
    }

    public void setSdkId(Integer sdkId) {
        this.sdkId = sdkId;
    }

    public String getParametersConfig() {
        return parametersConfig;
    }

    public void setParametersConfig(String parametersConfig) {
        this.parametersConfig = parametersConfig;
    }

    public String getLoginReqConfig() {
        return loginReqConfig;
    }

    public void setLoginReqConfig(String loginReqConfig) {
        this.loginReqConfig = loginReqConfig;
    }

    public String getLoginSignConfig() {
        return loginSignConfig;
    }

    public void setLoginSignConfig(String loginSignConfig) {
        this.loginSignConfig = loginSignConfig;
    }

    public String getLoginRespConfig() {
        return loginRespConfig;
    }

    public void setLoginRespConfig(String loginRespConfig) {
        this.loginRespConfig = loginRespConfig;
    }

    public String getOrderReqConfig() {
        return orderReqConfig;
    }

    public void setOrderReqConfig(String orderReqConfig) {
        this.orderReqConfig = orderReqConfig;
    }

    public String getOrderSignConfig() {
        return orderSignConfig;
    }

    public void setOrderSignConfig(String orderSignConfig) {
        this.orderSignConfig = orderSignConfig;
    }

    public String getOrderRespConfig() {
        return orderRespConfig;
    }

    public void setOrderRespConfig(String orderRespConfig) {
        this.orderRespConfig = orderRespConfig;
    }

    public String getLoginHandingBean() {
        return loginHandingBean;
    }

    public void setLoginHandingBean(String loginHandingBean) {
        this.loginHandingBean = loginHandingBean;
    }

    public String getOrderHandingBean() {
        return orderHandingBean;
    }

    public void setOrderHandingBean(String orderHandingBean) {
        this.orderHandingBean = orderHandingBean;
    }

    public Boolean getOpenPay() {
        return openPay;
    }

    public void setOpenPay(Boolean openPay) {
        this.openPay = openPay;
    }

    public Boolean getOpenLogin() {
        return openLogin;
    }

    public void setOpenLogin(Boolean openLogin) {
        this.openLogin = openLogin;
    }

    public Boolean getOpenRegister() {
        return openRegister;
    }

    public void setOpenRegister(Boolean openRegister) {
        this.openRegister = openRegister;
    }

    public String getCallbackConfig() {
        return callbackConfig;
    }

    public void setCallbackConfig(String callbackConfig) {
        this.callbackConfig = callbackConfig;
    }

    public String getCallbackSignConfig() {
        return callbackSignConfig;
    }

    public void setCallbackSignConfig(String callbackSignConfig) {
        this.callbackSignConfig = callbackSignConfig;
    }
}
