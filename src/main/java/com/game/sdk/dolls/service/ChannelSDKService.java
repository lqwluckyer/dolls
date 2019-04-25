package com.game.sdk.dolls.service;

import com.game.sdk.dolls.request.channelsdk.ChannelSDKReq;
import com.game.sdk.dolls.response.EasyUIResp;

public interface ChannelSDKService {
    EasyUIResp getChannelSDKList(ChannelSDKReq req);
    EasyUIResp saveChannelSDK(ChannelSDKReq req);
    EasyUIResp removeChannelSDK(ChannelSDKReq req);
    EasyUIResp getSimpleSDKList();
}
