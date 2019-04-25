package com.game.sdk.dolls.sdk;


import com.game.sdk.dolls.entity.Channel;
import com.game.sdk.dolls.entity.PayOrder;
import com.game.sdk.dolls.entity.Player;

/**
 * SDK操作脚本
 * 第三方登录认证的校验
 */
public interface SDKInterface {

    SDKLoginResult verifyToken(Channel channel, String extension, String authUrl);

    String getOrderInfo(Player player, PayOrder order, SDKOrderHandle callback);

}
