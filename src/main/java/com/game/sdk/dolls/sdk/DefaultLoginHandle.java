package com.game.sdk.dolls.sdk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.game.sdk.dolls.constants.CommonCode;
import com.game.sdk.dolls.constants.RedisKeyConst;
import com.game.sdk.dolls.entity.Channel;
import com.game.sdk.dolls.entity.ChannelSDK;
import com.game.sdk.dolls.entity.Game;
import com.game.sdk.dolls.entity.Player;
import com.game.sdk.dolls.request.player.LoginReq;
import com.game.sdk.dolls.response.BaseResp;
import com.game.sdk.dolls.service.PlayerService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service("defaultLoginHanding")
public class DefaultLoginHandle implements LoginHandle {

    private static final Logger logger = LoggerFactory.getLogger(DefaultLoginHandle.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PlayerService playerService;

    @Override
    @Transactional
    public BaseResp handle(Game game, Channel channel, ChannelSDK channelSDK, SDKLoginResult result, LoginReq req) {
        BaseResp resp = new BaseResp();
        try {
            logger.info("player verify success. result:{}, req:{}", result.getUserId(), JSON.toJSONString(req));
            if (result.isSuccess() && StringUtils.isNotBlank(result.getUserId())) {
                String key = channel.getGameId() + channel.getId() + result.getUserId();
                logger.info("player login start..., key:{}", key);

                Player player = playerService.getPlayerByChannel(channel.getGameId(), channel.getId(), result.getUserId());
                if (player == null) {
                    if (!channel.getOpenRegister()) {
                        resp.setCode(CommonCode.REGISTER_CLOSED);
                        return resp;
                    }
                    player = playerService.savePlayer(channel, result, req.getDeviceId(), req.getLoginIp(), channelSDK);
                } else {
                    if (StringUtils.isNoneBlank(result.getUserName())) {
                        player.setChannelUserName(result.getUserName());
                    }
                    if (StringUtils.isNoneBlank(result.getNickName())) {
                        player.setChannelUserNick(result.getNickName());
                    }
                    if (StringUtils.isNoneBlank(result.getExtension())) {
                        player.setExtension(result.getExtension());
                    }

                    player.setLoginIp(req.getLoginIp());
                    playerService.save(player);
                }
                playerService.saveLoginLog(player);
                String token = playerService.buildLoginToken(player, game.getGameSecret());
                String keyToken = RedisKeyConst.USER_LOGIN_TOKEN + token;
                String keyUserId = RedisKeyConst.USER_LOGIN_ID + player.getId();
                BoundValueOperations<String, String> valueOperations = redisTemplate.boundValueOps(keyUserId);
                String oldToken = valueOperations.get();
                if (StringUtils.isNotBlank(oldToken)) {
                    redisTemplate.delete(oldToken);
                }
                valueOperations.set(keyToken, 10, TimeUnit.DAYS);
                redisTemplate.boundValueOps(keyToken).set(String.valueOf(player.getId()), 10, TimeUnit.DAYS);
                JSONObject data = new JSONObject();
                data.put("userId", player.getId());
                data.put("sdkUserId", player.getChannelUserId());
                data.put("userName", player.getUserName());
                data.put("sdkUserName", player.getChannelUserName());
                data.put("token", token);
                data.put("extension", result.getExtension());
                data.put("timestamp", new Date());
                resp.setCode(CommonCode.SUCCESS);
                resp.setData(data);

            } else {
                logger.info("player login fail, req:{}, sdkResult:{}", JSON.toJSONString(req), JSON.toJSONString(result));
                resp.setCode(CommonCode.LOGIN_FAILED);
            }

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.info("player login exception and do rollback, req:" + JSON.toJSONString(req), e);
            resp.setCode(CommonCode.SYSTEM_ERROR);
        }
        return resp;
    }
}
