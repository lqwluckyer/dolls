package com.game.sdk.dolls.sdk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.game.sdk.dolls.constants.CommonCode;
import com.game.sdk.dolls.entity.Channel;
import com.game.sdk.dolls.entity.PayOrder;
import com.game.sdk.dolls.entity.Player;
import com.game.sdk.dolls.utils.HttpUtil;
import com.game.sdk.dolls.utils.SDKUtils;
import com.game.sdk.dolls.vo.SDKConfigVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class CommonMD5SDK implements SDKInterface {

    private static final Logger logger = LoggerFactory.getLogger(CommonMD5SDK.class);

    @Override
    public SDKLoginResult verifyToken(Channel channel, String extension, String authUrl) {
        try {
            String loginConfig = channel.getLoginReqConfig();
            if (StringUtils.isBlank(loginConfig)) {
                logger.info("channel loginConfig is null channelId:{}, extension:{}", channel.getId(), extension);
                return new SDKLoginResult(Boolean.FALSE, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
            }
            List<SDKConfigVo> sdkConfigList = JSONObject.parseArray(loginConfig, SDKConfigVo.class);

            String loginSignConfig = channel.getLoginSignConfig();
            if (StringUtils.isBlank(loginSignConfig)) {
                logger.info("channel loginSignConfig is null channelId:{}, extension:{}", channel.getId(), extension);
                return new SDKLoginResult(Boolean.FALSE, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
            }
            List<SDKConfigVo> signConfigList = JSONObject.parseArray(loginSignConfig, SDKConfigVo.class);

            if (StringUtils.isBlank(extension)) {
                logger.info("extension is null channelId:{}, extension:{}", channel.getId(), extension);
                return new SDKLoginResult(Boolean.FALSE, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
            }
            JSONObject exJson = JSONObject.parseObject(extension);

            String config = channel.getParametersConfig();
            if (StringUtils.isBlank(config)) {
                logger.info("parametersConfig is null channelId:{}, extension:{}", channel.getId(), extension);
                return new SDKLoginResult(Boolean.FALSE, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
            }
            JSONObject configJson = JSONObject.parseObject(config);

            Map<String, Object> paramsMap = SDKUtils.buildReqParams(sdkConfigList, exJson, configJson);
            String signKey = (String) paramsMap.get(SDKUtils.SIGN_KEY);
            paramsMap.remove(SDKUtils.SIGN_KEY);
            if (StringUtils.isNotBlank(signKey)) {
                // 有些SDK 可能不要进行签名， 所以 signKey 为空时，则不进行签名
                String signSource = SDKUtils.buildReqSign(signConfigList, exJson, configJson);
                if (StringUtils.isBlank(signSource)) {
                    logger.info("signSource is null channelId:{}, extension:{}", channel.getId(), extension);
                    return new SDKLoginResult(Boolean.FALSE, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
                }
                logger.info("signSource:{}", signSource);
                String sign = DigestUtils.md5Hex(signSource.getBytes(StandardCharsets.UTF_8));
                paramsMap.put(signKey, sign);
            }
            logger.info("check token start  url:{}, paramsMap:{}, channelId:{}", authUrl, JSON.toJSONString(paramsMap), channel.getId());
            int method = configJson.getIntValue(SDKUtils.METHOD);
            String response = null;
            if (method == CommonCode.NUMBER0) {
                // get 请求方式
                response = HttpUtil.sendGet(authUrl, paramsMap);
            } else if (method == CommonCode.NUMBER1) {
                // post form 表单
                response = HttpUtil.sendPost(authUrl, paramsMap);
            } else if (method == CommonCode.NUMBER2) {
                // post JSON 格式
                response = HttpUtil.sendPostJson(authUrl, JSON.toJSONString(paramsMap));
            } else {
                logger.info("http query method fail channelId:{}, ex:{}", channel.getId(), extension);
                return new SDKLoginResult(Boolean.FALSE, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
            }
            logger.info("check token end  url:{}, paramsMap:{}, channelId:{}, resp:{}", authUrl, JSON.toJSONString(paramsMap), channel.getId(), response);
            if (StringUtils.isBlank(response)) {
                return new SDKLoginResult(Boolean.FALSE, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
            }
            String respConfig = channel.getLoginRespConfig();
            if (StringUtils.isBlank(respConfig)) {
                logger.info("respConfig is null channelId:{}, ex:{}", channel.getId(), extension);
                return new SDKLoginResult(Boolean.FALSE, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
            }
            JSONObject respConfigJson = JSONObject.parseObject(respConfig);

            SDKLoginResult result = SDKUtils.buildResult(response, respConfigJson, exJson);
            if (result == null) {
                logger.info("check token fail  url:{}, paramsMap:{}, channelId:{}, resp:{}, respConfig:{}", authUrl, JSON.toJSONString(paramsMap), channel.getId(), response, respConfig);
                return new SDKLoginResult(Boolean.FALSE, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
            }
            return result;
        } catch (Exception e) {
            logger.info("CommonMD5SDK handle exception channelId:" + channel.getId() + " extension:" + extension, e);
            return new SDKLoginResult(Boolean.FALSE, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
        }
    }

    @Override
    public String getOrderInfo(Player player, PayOrder order, SDKOrderHandle callback) {
        return null;
    }
}
