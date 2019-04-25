package com.game.sdk.dolls.utils;

import com.alibaba.fastjson.JSONObject;
import com.game.sdk.dolls.constants.CommonCode;
import com.game.sdk.dolls.constants.SDKConfigTypeConst;
import com.game.sdk.dolls.entity.Game;
import com.game.sdk.dolls.entity.PayOrder;
import com.game.sdk.dolls.sdk.SDKLoginResult;
import com.game.sdk.dolls.vo.SDKConfigVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SDKUtils {

    public static final String SIGN_KEY = "signKey";
    public static final String EQ = "=";
    public static final String AND = "&";
    public static final String NULL_KEY = "nullKey";
    public static final String METHOD = "method";
    public static final String CHANNEL_USER_ID = "channelUserId";
    public static final String CHANNEL_USER_NAME = "channelUserName";
    public static final String CHANNEL_USER_NICK = "channelUserNick";
    public static final String RESPONSE_TYPE = "responseType";
    public static final String ORDER_RESPONSE_SUCCESS = "orderResponseSuccess";
    public static final String ORDER_RESPONSE_FAIL = "orderResponseFail";
    public static final String SUCCESS_KEY = "successKey";
    public static final String SUCCESS_VALUE = "successValue";
    public static final String DATA_KEY = "dataKey";
    public static final String ORDER_SIGN_TYPE = "orderSignType";
    public static final String ORDER_ID_KEY = "orderIdKey";
    public static final String CHANNEL_ORDER_ID = "channelOrderId";
    public static final String NOTIFY_SUCCESS = "SUCCESS";
    public static final String CHANNEL_SIGN_KEY = "channelSignKey";

    private SDKUtils() {
    }

    public static Map<String, Object> buildReqParams(List<SDKConfigVo> sdkConfigList, JSONObject exJson, JSONObject configJson) {
        Map<String, Object> paramsMap = new HashMap<>();
        for (SDKConfigVo sdkConfig : sdkConfigList) {
            if (sdkConfig.getType() == SDKConfigTypeConst.CHANNEL) {
                // 从 channel 的配置中获取值
                paramsMap.put(sdkConfig.getKey(), configJson.getString(sdkConfig.getValue()));
            } else if (sdkConfig.getType() == SDKConfigTypeConst.EXTENSION) {
                // 从客户端传过来的扩展参数中获取值
                paramsMap.put(sdkConfig.getKey(), exJson.getString(sdkConfig.getValue()));
            } else if (sdkConfig.getType() == SDKConfigTypeConst.SIGN) {
                // 请求参数的key名， 不同SDK可能这个 key  不一样
                paramsMap.put(SIGN_KEY, sdkConfig.getKey());
            }
        }
        return paramsMap;
    }

    public static String buildReqSign(List<SDKConfigVo> sdkConfigList, JSONObject exJson, JSONObject configJson) {
        StringBuilder signBuilder = new StringBuilder();

        for (SDKConfigVo sdkConfig : sdkConfigList) {
            if (sdkConfig.getType() == SDKConfigTypeConst.CHANNEL) {
                if (Objects.equals(NULL_KEY, sdkConfig.getKey())) {
                    signBuilder.append(configJson.getString(sdkConfig.getValue()));
                } else {
                    signBuilder.append(sdkConfig.getKey()).append(configJson.getString(sdkConfig.getValue()));
                }
            } else if (sdkConfig.getType() == SDKConfigTypeConst.EXTENSION) {
                if (Objects.equals(NULL_KEY, sdkConfig.getKey())) {
                    signBuilder.append(exJson.getString(sdkConfig.getValue()));
                } else {
                    signBuilder.append(sdkConfig.getKey()).append(exJson.getString(sdkConfig.getValue()));
                }
            }
        }

        return signBuilder.toString();
    }

    public static SDKLoginResult buildResult(String response, JSONObject respConfigJson, JSONObject exJson) {
        int respType = respConfigJson.getIntValue(SDKUtils.RESPONSE_TYPE);
        if (respType == CommonCode.NUMBER1) {
            // 响应数据中只要包含成功KEY， 渠道信息从扩展中获取
            if (response.contains(respConfigJson.getString(SDKUtils.SUCCESS_KEY))) {
                return setResult(exJson, respConfigJson);
            }
        } else if (respType == CommonCode.NUMBER2) {
            // 返回JSON格式数据，渠道信息从扩展参数获取
            JSONObject resultJson = JSONObject.parseObject(response);
            String key = respConfigJson.getString(SDKUtils.SUCCESS_KEY);
            String value = respConfigJson.getString(SDKUtils.SUCCESS_VALUE);
            if (Objects.equals(resultJson.getString(key), value)) {
                return setResult(exJson, respConfigJson);
            }
        } else if (respType == CommonCode.NUMBER3) {
            // 返回JSON格式数据，渠道信息从返回数据中获取
            JSONObject resultJson = JSONObject.parseObject(response);
            String key = respConfigJson.getString(SDKUtils.SUCCESS_KEY);
            String value = respConfigJson.getString(SDKUtils.SUCCESS_VALUE);
            if (Objects.equals(resultJson.getString(key), value)) {
                return setResult(resultJson, respConfigJson);
            }
        } else if (respType == CommonCode.NUMBER4) {
            // 返回JSON格式数据，渠道信息从返回数据中的下级JSON格式中获取
            JSONObject resultJson = JSONObject.parseObject(response);
            String key = respConfigJson.getString(SDKUtils.SUCCESS_KEY);
            String value = respConfigJson.getString(SDKUtils.SUCCESS_VALUE);
            if (Objects.equals(resultJson.getString(key), value)) {
                JSONObject resultDataJson = resultJson.getJSONObject(DATA_KEY);
                return setResult(resultDataJson, respConfigJson);
            }
        }
        return null;
    }

    public static SDKLoginResult setResult(JSONObject result, JSONObject config) {
        String channelUserId = result.getString(config.getString(SDKUtils.CHANNEL_USER_ID));
        Object channelUserName = result.get(config.getString(SDKUtils.CHANNEL_USER_NAME));
        Object channelUserNick = result.get(config.getString(SDKUtils.CHANNEL_USER_NICK));
        SDKLoginResult sdkResult = new SDKLoginResult(Boolean.TRUE
                , channelUserId
                , channelUserName == null ? null : channelUserName.toString()
                , channelUserNick == null ? null : channelUserNick.toString());

        return sdkResult;
    }

    public static String getNotifySign(PayOrder payOrder, Game game){
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append("appId=").append(game.getId())
                .append("&channelId=").append(payOrder.getChannelId())
                .append("&currency=").append(payOrder.getCurrency())
                .append("&extension=").append(payOrder.getExtension())
                .append("&money=").append(payOrder.getMoney())
                .append("&orderId=").append(payOrder.getId())
                .append("&playerId=").append(payOrder.getPlayerId())
                .append("&productId=").append(payOrder.getProductId())
                .append("&productName=").append(payOrder.getProductName())
                .append("&productDesc=").append(payOrder.getProductDesc())
                .append("&roleId=").append(payOrder.getRoleId())
                .append("&roleName=").append(payOrder.getRoleName())
                .append("&serverId=").append(payOrder.getServerId())
                .append("&serverName=").append(payOrder.getServerName())
                .append("&userName=").append(payOrder.getUserName());
        return signBuilder.toString();
    }


}
