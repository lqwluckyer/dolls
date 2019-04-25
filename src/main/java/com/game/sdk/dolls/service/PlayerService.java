package com.game.sdk.dolls.service;

import com.game.sdk.dolls.entity.Channel;
import com.game.sdk.dolls.entity.ChannelSDK;
import com.game.sdk.dolls.entity.LoginLog;
import com.game.sdk.dolls.entity.Player;
import com.game.sdk.dolls.request.player.LoginReq;
import com.game.sdk.dolls.request.player.SearchPlayerReq;
import com.game.sdk.dolls.request.player.SearchPlayerRoleReq;
import com.game.sdk.dolls.response.BaseResp;
import com.game.sdk.dolls.response.EasyUIResp;
import com.game.sdk.dolls.sdk.SDKLoginResult;

/**
 * Created by Administrator on 2018-12-13.
 */
public interface PlayerService {
    BaseResp getToken(LoginReq req);
    BaseResp loginVerify(LoginReq req);
    String buildLoginToken(Player player, String appSecret);
    Player getPlayerByPrimaryKey(Long id);
    Player getPlayerByChannel(Integer appId, Integer channelId, String channelUserId);
    Player savePlayer(Channel channel, SDKLoginResult result, String deviceId, String loginIp, ChannelSDK channelSDK);
    LoginLog saveLoginLog(Player player);
    Player save(Player player);

    EasyUIResp getPlayerList(SearchPlayerReq req);
    EasyUIResp getPlayerRoleList(SearchPlayerRoleReq req);

}
