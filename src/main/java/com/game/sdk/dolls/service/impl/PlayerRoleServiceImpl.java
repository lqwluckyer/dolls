package com.game.sdk.dolls.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.game.sdk.dolls.bean.SnowflakeWorkerId;
import com.game.sdk.dolls.cache.CacheManager;
import com.game.sdk.dolls.constants.CommonCode;
import com.game.sdk.dolls.dao.DaoSuper;
import com.game.sdk.dolls.dao.PlayerRoleDao;
import com.game.sdk.dolls.dao.RoleLogDao;
import com.game.sdk.dolls.entity.*;
import com.game.sdk.dolls.request.player.LoginReq;
import com.game.sdk.dolls.response.BaseResp;
import com.game.sdk.dolls.service.ChannelService;
import com.game.sdk.dolls.service.PlayerRoleService;
import com.game.sdk.dolls.service.PlayerService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.game.sdk.dolls.utils.SDKUtils.CHANNEL_SIGN_KEY;


/**
 * Created by Administrator on 2019-01-16.
 */
@Service
public class PlayerRoleServiceImpl implements PlayerRoleService {
    private static Logger logger = LoggerFactory.getLogger(PlayerRoleServiceImpl.class);
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private SnowflakeWorkerId workerId;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private PlayerRoleDao userRoleDao;

    @Autowired
    private RoleLogDao roleLogDao;

    @Autowired
    private DaoSuper daoSuper;

    @Override
    @Transactional
    public BaseResp upPlayerRole(LoginReq req) {
        BaseResp resp = new BaseResp();
        if(req.getAppId()==null || req.getUserId()==null || req.getChannelId()==null ||
                req.getServerId()==null || req.getRoleId()==null){
            logger.info("upPlayerRole reqParam is null req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.ARGS_ERROR);
            return resp;
        }

        Game game = cacheManager.getGame(req.getAppId());
        if (game == null) {
            logger.info("upPlayerRole game is null, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.GAME_NONE);
            return resp;
        }

        Channel channel = cacheManager.getChannel(req.getChannelId());
        if(channel == null){
            logger.info("upPlayerRole channel is null, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.CHANNEL_NONE);
            return resp;
        }
        if(!Objects.equals(game.getId(), channel.getGameId())){
            logger.info("upPlayerRole channelId not match game, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.ARGS_ERROR);
            return resp;
        }
        String config = channel.getParametersConfig();
        JSONObject json = JSON.parseObject(config);
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append("appId=").append(req.getAppId())
                .append("&channelId=").append(req.getChannelId())
                .append("&userId=").append(req.getUserId())
                .append("&serverId=").append(req.getServerId())
                .append("&serverName=").append(req.getServerName())
                .append("&serverType=").append(req.getServerType())
                .append("&roleId=").append(req.getRoleId())
                .append("&roleName=").append(req.getRoleName())
                .append("&roleLevel=").append(req.getRoleLevel())
                .append("&key=").append(json.get(CHANNEL_SIGN_KEY));
        String signSource = signBuilder.toString();
        logger.info("upPlayerRole signSource:{}", signSource);
        String newSign = DigestUtils.md5Hex(signSource.getBytes(StandardCharsets.UTF_8));
        if(!Objects.equals(newSign, req.getSign())){
            logger.info("upPlayerRole sign fail, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.SIGN_ERROR);
            return resp;
        }
        Player player = playerService.getPlayerByPrimaryKey(req.getUserId());
        if(player == null){
            logger.info("upPlayerRole player is null, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.USER_NONE);
            return resp;
        }

        PlayerRole playerRole = getPlayerRole(player, req);
        if(playerRole == null){
            logger.info("start save role, req:{}", JSON.toJSONString(req));
            playerRole = savePlayerRole(game, player, req);
        }
        logger.info("start save roleLog, req:{}", JSON.toJSONString(req));
        saveRoleLog(game, playerRole, req);
        resp.setCode(CommonCode.SUCCESS);
        return resp;
    }


    @Override
    public PlayerRole getPlayerRole(Player player, LoginReq req){
        String hql = "from PlayerRole where gameId=? and channelId=? and serverId=? and roleId=? and playerId=?";
        List params = new ArrayList();
        params.add(player.getGameId());
        params.add(player.getChannelId());
        params.add(req.getServerId());
        params.add(req.getRoleId());
        params.add(player.getId());
        PlayerRole playerRole = (PlayerRole)daoSuper.findUniqueByHql(hql, params);
        return playerRole;
    }

    private PlayerRole savePlayerRole(Game game, Player player, LoginReq req){
        PlayerRole playerRole =new PlayerRole();
        playerRole.setId(workerId.nextId());
        playerRole.setPlayerId(player.getId());
        playerRole.setGameId(game.getId());
        playerRole.setGameName(game.getGameName());
        playerRole.setServerId(req.getServerId());
        playerRole.setServerType(req.getServerType());
        playerRole.setServerName(req.getServerName());
        playerRole.setCreateTime(new Date());
        playerRole.setUserCreateTime(player.getCreateTime());
        playerRole.setRoleId(req.getRoleId());
        playerRole.setRoleName(req.getRoleName());
        playerRole.setChannelId(req.getChannelId());
        userRoleDao.save(playerRole);
        return playerRole;
    }

    private void saveRoleLog(Game game, PlayerRole playerRole, LoginReq req){
        RoleLog roleLog=new RoleLog();
        Date date=new Date();
        roleLog.setId(workerId.nextId());
        roleLog.setUserRoleId(playerRole.getId());
        roleLog.setGameId(game.getId());
        roleLog.setPlayerId(playerRole.getPlayerId());
        roleLog.setGameName(game.getGameName());
        roleLog.setServerId(playerRole.getServerId());
        roleLog.setServerType(playerRole.getServerType());
        roleLog.setServerName(playerRole.getServerName());
        roleLog.setCreateTime(date);
        roleLog.setRoleId(playerRole.getRoleId());
        roleLog.setRoleName(playerRole.getRoleName());
        roleLog.setRoleLevel(req.getRoleLevel());
        roleLog.setChannelId(playerRole.getChannelId());
        roleLog.setRoleCreateTime(playerRole.getCreateTime());
        roleLog.setLoginIp(req.getLoginIp());
        roleLogDao.save(roleLog);
    }
}
