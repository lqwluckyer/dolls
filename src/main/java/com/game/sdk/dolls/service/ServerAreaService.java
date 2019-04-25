package com.game.sdk.dolls.service;


import com.game.sdk.dolls.request.player.LoginReq;
import com.game.sdk.dolls.response.BaseResp;

/**
 * Created by Administrator on 2019-01-16.
 */
public interface ServerAreaService {
    BaseResp upServerArea(LoginReq req);
}
