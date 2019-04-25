package com.game.sdk.dolls.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 订单对象
 */

@Entity
@Table(name = "t_pay_order")
public class PayOrder extends BaseEntity{

    //订单号
    @Id
    private Long id;

    //当前所属游戏ID
    private Integer gameId;

    //当前所属渠道ID
    private Integer channelId;

    //t_player 表中ID
    private Long playerId;

    //t_player_role表中的ID
    private Long sysRoleId;

    //t_player 表中userName
    private String userName;

    //游戏中商品ID
    private String productId;

    //游戏中商品名称
    private String productName;

    //游戏中商品描述
    private String productDesc;

    //单位 分, 下单时收到的总金额，实际充值的金额以这个为准
    private Integer money;

    //单位 分，渠道SDK支付回调通知返回的总金额，记录，留作查账
    private Integer realMoney;

    // 物品单价
    private Integer price;

    // 购买数据
    private Integer buyNumbers;

    //币种 1 人民币
    private Integer currency;

    //游戏中角色ID
    private String roleId;

    //游戏中角色名称
    private String roleName;

    //服务器ID
    private String serverId;

    //服务器名称
    private String serverName;

    //订单状态
    private Integer payState;

    //渠道SDK对应的订单号
    private String channelOrderId;

    //扩展数据
    private String extension;

    //支付回调成功时间
    private Date sdkOrderTime;

    //发货成功时间
    private Date completeTime;

    //游戏下单的时候，可以携带notifyUrl过来，作为渠道支付回调时，通知到游戏服务器的地址，没有设置的话，默认走后台游戏管理中配置的固定通知回调地址
    private String notifyUrl;

    // 通知CP发货后收到的响应值
    private String cpResp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Long getSysRoleId() {
        return sysRoleId;
    }

    public void setSysRoleId(Long sysRoleId) {
        this.sysRoleId = sysRoleId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getRealMoney() {
        return realMoney;
    }

    public void setRealMoney(Integer realMoney) {
        this.realMoney = realMoney;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getBuyNumbers() {
        return buyNumbers;
    }

    public void setBuyNumbers(Integer buyNumbers) {
        this.buyNumbers = buyNumbers;
    }

    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Integer getPayState() {
        return payState;
    }

    public void setPayState(Integer payState) {
        this.payState = payState;
    }

    public String getChannelOrderId() {
        return channelOrderId;
    }

    public void setChannelOrderId(String channelOrderId) {
        this.channelOrderId = channelOrderId;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Date getSdkOrderTime() {
        return sdkOrderTime;
    }

    public void setSdkOrderTime(Date sdkOrderTime) {
        this.sdkOrderTime = sdkOrderTime;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getCpResp() {
        return cpResp;
    }

    public void setCpResp(String cpResp) {
        this.cpResp = cpResp;
    }
}
