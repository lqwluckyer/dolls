package com.game.sdk.dolls.sdk;

import com.game.sdk.dolls.entity.Channel;
import com.game.sdk.dolls.entity.ChannelSDK;
import com.game.sdk.dolls.entity.Game;
import com.game.sdk.dolls.request.player.LoginReq;
import com.game.sdk.dolls.response.BaseResp;

/**
 * Created by ant on 2015/4/7.
 */
public interface LoginHandle {

    BaseResp handle(Game game, Channel channel, ChannelSDK channelSDK, SDKLoginResult result, LoginReq req);

}
