package com.game.sdk.dolls.service.impl;

import com.alibaba.fastjson.JSON;
import com.game.sdk.dolls.bean.SnowflakeWorkerId;
import com.game.sdk.dolls.cache.CacheManager;
import com.game.sdk.dolls.constants.CommonCode;
import com.game.sdk.dolls.dao.ServerAreaDao;
import com.game.sdk.dolls.entity.Game;
import com.game.sdk.dolls.entity.ServerArea;
import com.game.sdk.dolls.request.player.LoginReq;
import com.game.sdk.dolls.response.BaseResp;
import com.game.sdk.dolls.service.ServerAreaService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2019-01-16.
 */
@Service
public class ServerAreaServiceImpl implements ServerAreaService {

    private static Logger logger = LoggerFactory.getLogger(ServerAreaServiceImpl.class);

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private SnowflakeWorkerId workerId;

    @Autowired
    private ServerAreaDao serverAreaDao;

    @Override
    public BaseResp upServerArea(LoginReq req) {
        BaseResp resp = new BaseResp();
        Game game = cacheManager.getGame(req.getAppId());
        if (game == null) {
            logger.info("upServerArea game is null, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.GAME_NONE);
            return resp;
        }
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append("appId=").append(req.getAppId())
                .append("&serverId=").append(req.getServerId())
                .append("&serverName=").append(req.getServerName())
                .append("&serverType=").append(req.getServerType())
                .append("&key=").append(game.getGameKey());
        String signSource = signBuilder.toString();
        logger.info("upPlayerRole signSource:{}", signSource);
        String newSign = DigestUtils.md5Hex(signSource.getBytes(StandardCharsets.UTF_8));
        if(!Objects.equals(newSign, req.getSign())){
            logger.info("upServerArea sign fail, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.SIGN_ERROR);
            return resp;
        }
        List<ServerArea> list = serverAreaDao.getServerAreaByGameId(game.getId(), req.getServerId());
        if(CollectionUtils.isNotEmpty(list)){
            resp.setCode(CommonCode.SUCCESS);
            return resp;
        }
        ServerArea serverArea=new ServerArea();
        serverArea.setId(workerId.nextId());
        serverArea.setGameId(game.getId());
        serverArea.setGameName(game.getGameName());
        serverArea.setServerId(req.getServerId());
        serverArea.setServerType(req.getServerType());
        serverArea.setServerName(req.getServerName());
        serverArea.setCreateTime(new Date());
        serverAreaDao.save(serverArea);
        resp.setCode(CommonCode.SUCCESS);
        return resp;
    }

}
