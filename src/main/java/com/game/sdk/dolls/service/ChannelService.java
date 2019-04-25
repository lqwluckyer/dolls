package com.game.sdk.dolls.service;

import com.game.sdk.dolls.request.channel.ChannelReq;
import com.game.sdk.dolls.response.EasyUIResp;

/**
 * Created by Administrator on 2019-01-22.
 */
public interface ChannelService {

    EasyUIResp getChannelByGameId(Integer gameId);
    EasyUIResp getChannelList(ChannelReq req);
    EasyUIResp saveChannel(ChannelReq req);
    EasyUIResp removeChannel(ChannelReq req);
    EasyUIResp getSimpleChannel();
}
