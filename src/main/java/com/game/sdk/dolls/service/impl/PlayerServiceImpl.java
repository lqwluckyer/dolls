package com.game.sdk.dolls.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.game.sdk.dolls.bean.SnowflakeWorkerId;
import com.game.sdk.dolls.bean.SpringUtils;
import com.game.sdk.dolls.cache.CacheManager;
import com.game.sdk.dolls.cache.SDKCacheManager;
import com.game.sdk.dolls.constants.CommonCode;
import com.game.sdk.dolls.constants.RedisKeyConst;
import com.game.sdk.dolls.dao.DaoSuper;
import com.game.sdk.dolls.dao.LoginLogDao;
import com.game.sdk.dolls.dao.PlayerDao;
import com.game.sdk.dolls.entity.*;
import com.game.sdk.dolls.request.player.LoginReq;
import com.game.sdk.dolls.request.player.SearchPlayerReq;
import com.game.sdk.dolls.request.player.SearchPlayerRoleReq;
import com.game.sdk.dolls.response.BaseResp;
import com.game.sdk.dolls.response.EasyUIResp;
import com.game.sdk.dolls.sdk.LoginHandle;
import com.game.sdk.dolls.sdk.SDKInterface;
import com.game.sdk.dolls.sdk.SDKLoginResult;
import com.game.sdk.dolls.service.PlayerService;
import com.game.sdk.dolls.utils.PageUtils;
import com.game.sdk.dolls.vo.PlayerRoleVo;
import com.game.sdk.dolls.vo.PlayerVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2018-12-13.
 */
@Service
public class PlayerServiceImpl implements PlayerService {
    private static Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private SnowflakeWorkerId workerId;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PlayerDao userDao;
    @Autowired
    private LoginLogDao loginLogDao;

    @Autowired
    private DaoSuper daoSuper;

    @Autowired(required = false)
    @Qualifier("defaultLoginHanding")
    private LoginHandle loginHandle;

    @Override
    public BaseResp getToken(LoginReq req) {
        BaseResp resp = new BaseResp();
        if (req.getAppId() == null) {
            logger.info("gameId is null, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.GAME_NONE);
            return resp;
        }
        Game game = cacheManager.getGame(req.getAppId());
        if (game == null) {
            logger.info("gameId is null, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.GAME_NONE);
            return resp;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("appId=").append(req.getAppId())
                .append("&channelId=").append(req.getChannelId())
                .append("&extension=").append(req.getExtension())
                .append("&deviceId=").append(req.getDeviceId())
                .append("&key=").append(game.getGameSecret());
        String signSource = sb.toString();
        logger.info("sign source:{}", signSource);
        String newSign = DigestUtils.md5Hex(signSource.getBytes(StandardCharsets.UTF_8));
        if (req.getSign() == null || !Objects.equals(newSign.toLowerCase(), req.getSign().toLowerCase())) {
            logger.info("the sign is invalid. sign:{}, req:{}, newSign:{}", req.getSign(), JSON.toJSONString(req), newSign);
            resp.setCode(CommonCode.SIGN_ERROR);
            return resp;
        }
        if (req.getChannelId() == null) {
            logger.info("channelId is null, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.CHANNEL_NONE);
            return resp;
        }
        Channel channel = cacheManager.getChannel(req.getChannelId());
        if (channel == null) {
            logger.info("channelId is null, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.CHANNEL_NONE);
            return resp;
        }

        if (!Objects.equals(channel.getGameId(), req.getAppId())) {
            logger.info("the game no config the channel, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.CHANNEL_NOT_MATCH);
            return resp;
        }

        if (!channel.getOpenLogin()) {
            logger.info("the channel close login, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.LOGIN_CLOSED);
            return resp;
        }

        if (channel.getSdkId() == null) {
            logger.info("sdkId is null, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.SDK_NONE);
            return resp;
        }
        ChannelSDK channelSDK = cacheManager.getChannelSDK(channel.getSdkId());
        if (channelSDK == null) {
            logger.info("sdkId is null, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.SDK_NONE);
            return resp;
        }
        SDKInterface sdk = SDKCacheManager.getInstance().getSDKInstance(channelSDK);
        if (sdk == null) {
            logger.info("the SDKInterface is not found, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.VERIFY_FAILED);
            return resp;
        }

        logger.info("SDKInterface start run req:{}", JSON.toJSONString(req));
        SDKLoginResult result = sdk.verifyToken(channel, req.getExtension(), channelSDK.getAuthUrl());
        if (result == null) {
            logger.info("the SDKInterface check token fail, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.VERIFY_FAILED);
            return resp;
        }
        if (!result.isSuccess()) {
            logger.info("the SDKInterface check token fail, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.VERIFY_FAILED);
            return resp;
        }
        if (StringUtils.isBlank(channel.getLoginHandingBean())) {
            return loginHandle.handle(game, channel, channelSDK, result, req);
        }
        LoginHandle handle = (LoginHandle) SpringUtils.getBean(channel.getLoginHandingBean());
        return handle.handle(game, channel, channelSDK, result, req);
    }

    @Override
    public BaseResp loginVerify(LoginReq req) {
        BaseResp resp = new BaseResp();
        BoundValueOperations<String, String> boundOperations = redisTemplate.boundValueOps(RedisKeyConst.USER_LOGIN_TOKEN + req.getToken());
        if (boundOperations == null) {
            logger.info("loginVerify player no login, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.TOKEN_ERROR);
            return resp;
        }
        String playerId = boundOperations.get();
        if (StringUtils.isBlank(playerId)) {
            logger.info("loginVerify player no login, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.VERIFY_FAILED);
            return resp;
        }
        Long primaryKey = Long.parseLong(playerId);
        if (!Objects.equals(primaryKey, req.getUserId())) {
            logger.info("loginVerify userId is not eq , req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.VERIFY_FAILED);
            return resp;
        }
        Player player = getPlayerByPrimaryKey(primaryKey);
        if (player == null) {
            logger.info("loginVerify player is null, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.VERIFY_FAILED);
            return resp;
        }

        Game game = cacheManager.getGame(player.getGameId());
        if (game == null) {
            logger.info("loginVerify game is null, req:{}", JSON.toJSONString(req));
            resp.setCode(CommonCode.VERIFY_FAILED);
            return resp;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("userId=").append(req.getUserId())
                .append("&token=").append(req.getToken())
                .append("&key=").append(game.getGameSecret());

        String signSource = sb.toString();

        logger.info("loginVerify signSource:{}", signSource);
        String newSign = DigestUtils.md5Hex(signSource.getBytes(StandardCharsets.UTF_8));
        if (!Objects.equals(newSign.toLowerCase(), req.getSign().toLowerCase())) {
            logger.info("loginVerify sign error , req:{}, newSign:{}", JSON.toJSONString(req), newSign);
            resp.setCode(CommonCode.SIGN_ERROR);
            return resp;
        }

        JSONObject data = new JSONObject();
        data.put("userId", player.getId());
        data.put("userName", player.getUserName());
        resp.setData(data);
        resp.setCode(CommonCode.SUCCESS);
        return resp;
    }

    @Override
    public String buildLoginToken(Player player, String appSecret) {

        StringBuilder sb = new StringBuilder();
        sb.append(player.getId()).append("-")
                .append(player.getGameId()).append("-")
                .append(player.getChannelId()).append("-")
                .append(player.getChannelUserId()).append("-")
                .append(System.currentTimeMillis()).append("-")
                .append(appSecret);
        return DigestUtils.md5Hex(sb.toString().getBytes(StandardCharsets.UTF_8));

    }

    @Override
    public Player getPlayerByPrimaryKey(Long id) {
        if (id == null) {
            return null;
        }
        return userDao.getOne(id);
    }

    @Override
    public Player savePlayer(Channel channel, SDKLoginResult result, String deviceId, String loginIp, ChannelSDK channelSDK) {
        Player player = new Player();
        player.setId(workerId.nextId());
        player.setGameId(channel.getGameId());
        player.setChannelId(channel.getId());
        player.setUserName(System.currentTimeMillis() + channelSDK.getSdkShortName());
        player.setChannelUserId(result.getUserId());
        player.setChannelUserName(result.getUserName());
        player.setChannelUserNick(result.getNickName());
        player.setCreateTime(new Date());
        player.setDeviceId(deviceId);
        player.setLoginIp(loginIp);
        player.setExtension(result.getExtension());
        userDao.saveAndFlush(player);
        return player;
    }

    @Override
    public LoginLog saveLoginLog(Player player) {
        LoginLog loginLog = new LoginLog();
        loginLog.setId(workerId.nextId());
        loginLog.setPlayerId(player.getId());
        loginLog.setGameId(player.getGameId());
        loginLog.setChannelId(player.getChannelId());
        loginLog.setUserName(player.getUserName());
        loginLog.setChannelUserId(player.getChannelUserId());
        loginLog.setChannelUserName(player.getChannelUserName());
        loginLog.setChannelUserNick(player.getChannelUserNick());
        loginLog.setUserCreateTime(player.getCreateTime());
        loginLog.setLastLoginTime(new Date());
        loginLog.setLoginIp(player.getLoginIp());
        loginLogDao.save(loginLog);

        return loginLog;
    }

    @Override
    public Player getPlayerByChannel(Integer appId, Integer channelId, String channelUserId) {
        String sql = "from Player where gameId=? and channelId=? and channelUserId=?";
        List params = new ArrayList();
        params.add(appId);
        params.add(channelId);
        params.add(channelUserId);
        Player player = (Player) daoSuper.findUniqueByHql(sql, params);
        return player;
    }

    @Override
    public Player save(Player player) {
        if (player == null) {
            return null;
        }
        return userDao.save(player);
    }

    @Override
    public EasyUIResp getPlayerList(SearchPlayerReq req) {
        //构建对象
        EasyUIResp resp = new EasyUIResp();
        if(req.getId() ==null && (req.getGameId()==null || req.getChannelId()==null)){
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("游戏或用户ID为空");
            return resp;
        }
        StringBuilder sqlBuilder = new StringBuilder("select * from t_player where ");
        StringBuilder countBuilder = new StringBuilder("select count(*) from t_player where ");
        List params = new ArrayList();
        boolean hasParams = Boolean.FALSE;
        if(req.getId()!=null){
            sqlBuilder.append(" id=?");
            countBuilder.append(" id=?");
            params.add(req.getId());
            hasParams = Boolean.TRUE;
        }
        if(req.getGameId()!=null){
            if(hasParams){
                sqlBuilder.append(" and game_id=?");
                countBuilder.append(" and game_id=?");
            }else{
                sqlBuilder.append(" game_id=?");
                countBuilder.append(" game_id=?");
            }
            params.add(req.getGameId());
            hasParams = Boolean.TRUE;
        }
        if(req.getChannelId()!=null){
            if(hasParams){
                sqlBuilder.append(" and channel_id=?");
                countBuilder.append(" and channel_id=?");
            }else{
                sqlBuilder.append(" channel_id=?");
                countBuilder.append(" channel_id=?");
            }
            params.add(req.getChannelId());
            hasParams = Boolean.TRUE;
        }
        if(StringUtils.isNotBlank(req.getChannelUserId())){
            if(hasParams){
                sqlBuilder.append(" and channel_user_id=?");
                countBuilder.append(" and channel_user_id=?");
            }else{
                sqlBuilder.append(" channel_user_id=?");
                countBuilder.append(" channel_user_id=?");
            }
            params.add(req.getChannelUserId());
            hasParams = Boolean.TRUE;
        }
        if(StringUtils.isNotBlank(req.getStartTime())){
            if(hasParams){
                sqlBuilder.append(" and create_time>=?");
                countBuilder.append(" and create_time>=?");
            }else{
                sqlBuilder.append(" create_time>=?");
                countBuilder.append(" create_time>=?");
            }
            params.add(req.getStartTime());
            hasParams = Boolean.TRUE;
        }
        if(StringUtils.isNotBlank(req.getEndTime())){
            if(hasParams){
                sqlBuilder.append(" and create_time<=?");
                countBuilder.append(" and create_time<=?");
            }else{
                sqlBuilder.append(" create_time<=?");
                countBuilder.append(" create_time<=?");
            }
            params.add(req.getEndTime());
            hasParams = Boolean.TRUE;
        }
        if(hasParams){
            sqlBuilder.append(" and state=?");
            countBuilder.append(" and state=?");
        }else{
            sqlBuilder.append(" state=?");
            countBuilder.append(" state=?");
        }
        params.add(CommonCode.NUMBER1);

        long countNum = daoSuper.count(countBuilder.toString(), params);

        if(countNum<=0){
            resp.setCode(CommonCode.SUCCESS);
            resp.setTotal(Long.valueOf(CommonCode.NUMBER0));
            return resp;
        }
        sqlBuilder.append(" order by id desc limit ?,?");
        params = PageUtils.builderPageParams(params, req.getPage(), req.getRows());
        List<Player> list = daoSuper.findBySql(sqlBuilder.toString(), params, Player.class);
        List<PlayerVo> data = new ArrayList<>();
        for (Player c : list) {
            PlayerVo vo = new PlayerVo();
            BeanUtils.copyProperties(c, vo);
            data.add(vo);
        }
        resp.setCode(CommonCode.SUCCESS);
        resp.setRows(data);
        resp.setTotal(countNum);
        return resp;
    }

    @Override
    public EasyUIResp getPlayerRoleList(SearchPlayerRoleReq req) {
        EasyUIResp resp = new EasyUIResp();
        if((req.getGameId()==null || req.getChannelId()==null)){
            resp.setCode(CommonCode.ARGS_ERROR);
            resp.setMsg("游戏或渠道为空");
            return resp;
        }
        StringBuilder sqlBuilder = new StringBuilder("select * from t_player_role where game_id=? and channel_id=? ");
        StringBuilder countBuilder = new StringBuilder("select count(*) from t_player_role where game_id=? and channel_id=? ");
        List params = new ArrayList();

        params.add(req.getGameId());
        params.add(req.getChannelId());

        if(StringUtils.isNotBlank(req.getRoleName())){
            sqlBuilder.append(" and role_name=?");
            countBuilder.append(" and role_name=?");
            params.add(req.getRoleName());

        }
        if(req.getPlayerId()!=null){
            sqlBuilder.append(" and player_id=?");
            countBuilder.append(" and player_id=?");
            params.add(req.getPlayerId());

        }
        if(StringUtils.isNotBlank(req.getStartTime())){
            sqlBuilder.append(" and create_time>=?");
            countBuilder.append(" and create_time>=?");
            params.add(req.getStartTime());

        }
        if(StringUtils.isNotBlank(req.getEndTime())){
            sqlBuilder.append(" and create_time<=?");
            countBuilder.append(" and create_time<=?");
            params.add(req.getEndTime());

        }
        sqlBuilder.append(" and state=?");
        countBuilder.append(" and state=?");
        params.add(CommonCode.NUMBER1);

        long countNum = daoSuper.count(countBuilder.toString(), params);

        if(countNum<=0){
            resp.setCode(CommonCode.SUCCESS);
            resp.setTotal(Long.valueOf(CommonCode.NUMBER0));
            return resp;
        }
        sqlBuilder.append(" order by id desc limit ?,?");
        params = PageUtils.builderPageParams(params, req.getPage(), req.getRows());
        List<PlayerRole> list = daoSuper.findBySql(sqlBuilder.toString(), params, PlayerRole.class);
        List<PlayerRoleVo> data = new ArrayList<>();
        for (PlayerRole c : list) {
            PlayerRoleVo vo = new PlayerRoleVo();
            BeanUtils.copyProperties(c, vo);
            data.add(vo);
        }
        resp.setCode(CommonCode.SUCCESS);
        resp.setRows(data);
        resp.setTotal(countNum);
        return resp;
    }
}
