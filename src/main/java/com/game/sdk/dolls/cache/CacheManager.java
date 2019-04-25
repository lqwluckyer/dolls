package com.game.sdk.dolls.cache;

import com.alibaba.fastjson.JSON;
import com.game.sdk.dolls.constants.RedisKeyConst;
import com.game.sdk.dolls.dao.ChannelDao;
import com.game.sdk.dolls.dao.ChannelSDKDao;
import com.game.sdk.dolls.dao.GameDao;
import com.game.sdk.dolls.entity.Channel;
import com.game.sdk.dolls.entity.ChannelSDK;
import com.game.sdk.dolls.entity.Game;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/***
 * 将常用的数据进行缓存。包含game,sdk,channel等对象
 */
@Component
public class CacheManager {

    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private GameDao gameDao;

    @Autowired
    private ChannelDao channelDao;

    @Autowired
    private ChannelSDKDao channelSDKDao;

    public Game getGame(Integer appId) {
        BoundValueOperations<String, String> boundOperations = redisTemplate.boundValueOps(RedisKeyConst.GAMES_KEY_PREFIX + String.valueOf(appId));
        if (boundOperations == null) {
            Game entity = gameDao.getOne(appId);
            if (entity != null) {
                ValueOperations<String, String> operation = redisTemplate.opsForValue();
                String valueJson = JSON.toJSONString(entity);
                operation.set(RedisKeyConst.GAMES_KEY_PREFIX + String.valueOf(appId), valueJson, 30, TimeUnit.DAYS);
                return entity;
            }
            logger.info("query by gameId is null, gameId:{}", appId);
            return null;
        }
        String value = boundOperations.get();
        if (StringUtils.isBlank(value)) {
            Game entity = gameDao.getOne(appId);
            if (entity != null) {
                String valueJson = JSON.toJSONString(entity);
                boundOperations.set(valueJson, 30, TimeUnit.DAYS);
                return entity;
            }
            logger.info("query by gameId is null, gameId:{}", appId);
            return null;
        }
        Game entity = JSON.parseObject(value, Game.class);
        return entity;
    }

    public ChannelSDK getChannelSDK(Integer sdkId) {

        BoundValueOperations<String, String> boundOperations = redisTemplate.boundValueOps(RedisKeyConst.CHANNEL_SDK_KEY_PREFIX + String.valueOf(sdkId));
        if (boundOperations == null) {
            ChannelSDK entity = channelSDKDao.getOne(sdkId);
            if (entity != null) {
                ValueOperations<String, String> operation = redisTemplate.opsForValue();
                String valueJson = JSON.toJSONString(entity);
                operation.set(RedisKeyConst.CHANNEL_SDK_KEY_PREFIX + String.valueOf(sdkId), valueJson, 30, TimeUnit.DAYS);
                return entity;
            }
            logger.info("query by sdk is null, gameId:{}", sdkId);
            return null;
        }
        String value = boundOperations.get();
        if (StringUtils.isBlank(value)) {
            ChannelSDK entity = channelSDKDao.getOne(sdkId);
            if (entity != null) {
                String valueJson = JSON.toJSONString(entity);
                boundOperations.set(valueJson, 30, TimeUnit.DAYS);
                return entity;
            }
            logger.info("query by sdk is null, gameId:{}", sdkId);
            return null;
        }
        ChannelSDK entity = JSON.parseObject(value, ChannelSDK.class);
        return entity;
    }

    public Channel getChannel(Integer channelId) {
        BoundValueOperations<String, String> boundOperations = redisTemplate.boundValueOps(RedisKeyConst.CHANNEL_KEY_PREFIX + String.valueOf(channelId));
        if (boundOperations == null) {
            Channel entity = channelDao.getOne(channelId);
            if (entity != null) {
                ValueOperations<String, String> operation = redisTemplate.opsForValue();
                String valueJson = JSON.toJSONString(entity);
                operation.set(RedisKeyConst.CHANNEL_KEY_PREFIX + String.valueOf(channelId), valueJson, 30, TimeUnit.DAYS);
                return entity;
            }
            logger.info("query by channelId is null, gameId:{}", channelId);
            return null;
        }
        String value = boundOperations.get();
        if (StringUtils.isBlank(value)) {
            Channel entity = channelDao.getOne(channelId);
            if (entity != null) {
                String valueJson = JSON.toJSONString(entity);
                boundOperations.set(valueJson, 30, TimeUnit.DAYS);
                return entity;
            }
            logger.info("query by channelId is null, gameId:{}", channelId);
            return null;
        }
        Channel entity = JSON.parseObject(value, Channel.class);
        return entity;
    }


    public void saveGame(Game game) {
        ValueOperations<String, String> operation = redisTemplate.opsForValue();
        String valueJson = JSON.toJSONString(game);
        operation.set(RedisKeyConst.GAMES_KEY_PREFIX + String.valueOf(game.getId()), valueJson, 30, TimeUnit.DAYS);
    }
    public void removeGame(Integer appId) {
        redisTemplate.delete(RedisKeyConst.GAMES_KEY_PREFIX + String.valueOf(appId));
    }

    public void saveChannelSDK(ChannelSDK sdk) {
        ValueOperations<String, String> operation = redisTemplate.opsForValue();
        String valueJson = JSON.toJSONString(sdk);
        operation.set(RedisKeyConst.CHANNEL_SDK_KEY_PREFIX + String.valueOf(sdk.getId()), valueJson, 30, TimeUnit.DAYS);
    }

    public void removeChannelSDK(Integer sdkId) {
        redisTemplate.delete(RedisKeyConst.CHANNEL_SDK_KEY_PREFIX + String.valueOf(sdkId));
    }


    //添加或者修改渠道
    public void saveChannel(Channel channel) {
        ValueOperations<String, String> operation = redisTemplate.opsForValue();
        String valueJson = JSON.toJSONString(channel);
        operation.set(RedisKeyConst.CHANNEL_KEY_PREFIX + String.valueOf(channel.getId()), valueJson, 30, TimeUnit.DAYS);

    }

    public void removeChannel(Integer channelId) {
        redisTemplate.delete(RedisKeyConst.CHANNEL_KEY_PREFIX + String.valueOf(channelId));
    }
}
