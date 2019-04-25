package com.game.sdk.dolls.sdk;


import com.game.sdk.dolls.entity.Channel;
import com.game.sdk.dolls.entity.ChannelSDK;
import com.game.sdk.dolls.entity.PayOrder;
import com.game.sdk.dolls.entity.Player;
import com.game.sdk.dolls.response.BaseResp;

public interface SDKOrderHandle {

    BaseResp handleChannelOrder(PayOrder payOrder, Player player, Channel channel, ChannelSDK sdk);

}
