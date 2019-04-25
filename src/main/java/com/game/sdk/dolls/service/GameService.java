package com.game.sdk.dolls.service;

import com.game.sdk.dolls.request.game.GameReq;
import com.game.sdk.dolls.response.EasyUIResp;

public interface GameService {
    EasyUIResp getGameList(GameReq req);
    EasyUIResp saveGame(GameReq req);
    EasyUIResp removeGame(GameReq req);
    EasyUIResp getSimpleGame();
}
