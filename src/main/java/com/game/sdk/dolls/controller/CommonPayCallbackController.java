package com.game.sdk.dolls.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.game.sdk.dolls.cache.CacheManager;
import com.game.sdk.dolls.constants.CommonCode;
import com.game.sdk.dolls.constants.PayState;
import com.game.sdk.dolls.constants.SDKConfigTypeConst;
import com.game.sdk.dolls.entity.Channel;
import com.game.sdk.dolls.entity.Game;
import com.game.sdk.dolls.entity.PayOrder;
import com.game.sdk.dolls.response.BaseResp;
import com.game.sdk.dolls.sdk.NotifyService;
import com.game.sdk.dolls.service.PayOrderService;
import com.game.sdk.dolls.utils.SDKUtils;
import com.game.sdk.dolls.vo.SDKConfigVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.game.sdk.dolls.utils.SDKUtils.*;

@RestController
@RequestMapping("/api/common/pay/callback")
public class CommonPayCallbackController {

    private static final Logger logger = LoggerFactory.getLogger(CommonPayCallbackController.class);

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PayOrderService payOrderService;

    @RequestMapping(value = "/form/{channelId}", method = {RequestMethod.POST})
    public String form(HttpServletRequest request, @PathVariable String channelId) {
        BaseResp resp = new BaseResp();
        logger.info("form callback start channelId:{}, req:{}", channelId, JSON.toJSONString(request.getParameterMap()));
        try {
            if (StringUtils.isBlank(channelId)) {
                logger.info("callback fail channelId is null");
                resp.setCode(CommonCode.CHANNEL_NONE);
                return JSON.toJSONString(resp);
            }
            Channel channel = cacheManager.getChannel(Integer.valueOf(channelId));
            if (channel == null) {
                logger.info("callback fail channel is null, channelId:{}", channelId);
                resp.setCode(CommonCode.CHANNEL_NONE);
                return JSON.toJSONString(resp);
            }

            String callback = channel.getCallbackConfig();
            if (StringUtils.isBlank(callback)) {
                logger.info("callback fail callbackConfig is null, channelId:{}", channelId);
                resp.setCode(CommonCode.CHANNEL_NONE);
                return JSON.toJSONString(resp);
            }
            JSONObject callbackJson = JSONObject.parseObject(callback);
            String orderId = request.getParameter(callbackJson.getString(ORDER_ID_KEY));
            if (StringUtils.isBlank(orderId)) {
                logger.info("callback fail orderId is null, channelId:{}", channelId);
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
            }
            PayOrder payOrder = payOrderService.getPayOrderWithLock(Long.valueOf(orderId));
            if (payOrder == null) {
                logger.info("callback fail payOrder is null, channelId:{},orderId:{}", channelId, orderId);
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
            }
            Game game = cacheManager.getGame(payOrder.getGameId());
            if (game == null) {
                logger.info("callback fail game is null, channelId:{},orderId:{}", channelId, orderId);
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
            }
            if (!Objects.equals(channel.getId(), payOrder.getChannelId())) {
                logger.info("callback fail channelId not match, channelId:{},orderId:{}", channelId, orderId);
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
            }

            String signConfig = channel.getCallbackSignConfig();
            if (StringUtils.isBlank(signConfig)) {
                logger.info("callback fail signConfig is null, channelId:{},orderId:{}", channelId, orderId);
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
            }
            // 处理回调签名
            List<SDKConfigVo> sdkConfigList = JSON.parseArray(signConfig, SDKConfigVo.class);
            StringBuilder signBuilder = new StringBuilder();
            for (SDKConfigVo sdkConfig : sdkConfigList) {
                if (sdkConfig.getType() == SDKConfigTypeConst.CHANNEL) {
                    if (Objects.equals(NULL_KEY, sdkConfig.getKey())) {
                        signBuilder.append(callbackJson.getString(sdkConfig.getValue()));
                    } else {
                        signBuilder.append(sdkConfig.getKey()).append(callbackJson.getString(sdkConfig.getValue()));
                    }
                } else if (sdkConfig.getType() == SDKConfigTypeConst.EXTENSION) {
                    if (Objects.equals(NULL_KEY, sdkConfig.getKey())) {
                        signBuilder.append(request.getParameter(sdkConfig.getValue()));
                    } else {
                        signBuilder.append(sdkConfig.getKey()).append(request.getParameter(sdkConfig.getValue()));
                    }
                }
            }

            String signSource = signBuilder.toString();
            logger.info("sign source:{}, orderId:{}", signSource, orderId);
            String sign = DigestUtils.md5Hex(signSource.getBytes(StandardCharsets.UTF_8));
            if (!Objects.equals(sign, request.getParameter(callbackJson.getString(SDKUtils.SIGN_KEY)))) {
                logger.info("sign fail ,orderId:{}, channelId:{}", orderId, channelId);
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
            }

            if (payOrder.getPayState() == PayState.STATE_SUCCESS) {
                logger.info("order state is success, orderId:{}", payOrder.getId());
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_SUCCESS);
            }
            if (payOrder.getPayState() != PayState.STATE_PAYING) {
                logger.info("order state is fail, orderId:{}, state:{}", payOrder.getId(), payOrder.getPayState());
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
            }

            Object successKey = callbackJson.get(SDKUtils.SUCCESS_KEY);

            // 开始处理支付订单
            if (successKey != null) {
                //  根据回调参数判断是否处理该单
                String successValue = request.getParameter(successKey.toString());
                if (!Objects.equals(callbackJson.getString(SDKUtils.SUCCESS_VALUE), successValue)) {
                    logger.info("channel pay fail, orderId:{}, channelId:{}", orderId, channelId);
                    return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
                }
            }
            String money = request.getParameter(callbackJson.getString(DATA_KEY));
            Integer moneyInt = new BigDecimal(money).multiply(new BigDecimal(100)).intValue();
            if (!Objects.equals(payOrder.getMoney(), moneyInt)
                    && !Objects.equals(payOrder.getMoney(), Integer.valueOf(money))) {
                logger.info("money is not match, orderId:{}, channelId:{}", orderId, channelId);
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
            }
            payOrder.setPayState(PayState.STATE_HANDING);
            payOrder.setSdkOrderTime(new Date());
            payOrder.setChannelOrderId(request.getParameter(callbackJson.getString(SDKUtils.CHANNEL_ORDER_ID)));
            payOrderService.savePayOrder(payOrder);
            logger.info("order callback success, orderId:{}", orderId);
            NotifyService.notify(payOrder, payOrderService, game);
            return callbackJson.getString(SDKUtils.ORDER_RESPONSE_SUCCESS);
        } catch (Exception e) {
            logger.error("form callback exception channelId:" + channelId + " req:"+JSON.toJSONString(request.getParameterMap()), e);
            resp.setCode(CommonCode.SYSTEM_ERROR);
            return JSON.toJSONString(resp);
        }
    }

    @RequestMapping(value = "/flow/{channelId}", method = {RequestMethod.POST})
    public String flow(HttpServletRequest request, @PathVariable String channelId) {
        BaseResp resp = new BaseResp();
        StringBuilder builder = new StringBuilder();
        try {

            if (StringUtils.isBlank(channelId)) {
                logger.info("callback fail channelId is null");
                resp.setCode(CommonCode.CHANNEL_NONE);
                return JSON.toJSONString(resp);
            }
            Channel channel = cacheManager.getChannel(Integer.valueOf(channelId));
            if (channel == null) {
                logger.info("callback fail channel is null, channelId:{}", channelId);
                resp.setCode(CommonCode.CHANNEL_NONE);
                return JSON.toJSONString(resp);
            }

            String callback = channel.getCallbackConfig();
            if (StringUtils.isBlank(callback)) {
                logger.info("callback fail callbackConfig is null, channelId:{}", channelId);
                resp.setCode(CommonCode.CHANNEL_NONE);
                return JSON.toJSONString(resp);
            }
            JSONObject callbackJson = JSONObject.parseObject(callback);

            BufferedReader reader = request.getReader();

            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            String reqData = builder.toString();
            if (StringUtils.isBlank(reqData)) {
                logger.info("reqData is null, channelId:{}", channelId);
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
            }
            logger.info("callback reqData:{}, channelId:{}", reqData, channelId);
            JSONObject reqDataJson = JSONObject.parseObject(reqData);
            Object orderId = reqDataJson.get(callbackJson.getString(ORDER_ID_KEY));
            if (orderId == null) {
                logger.info("callback fail orderId is null, channelId:{}", channelId);
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
            }
            PayOrder payOrder = payOrderService.getPayOrderWithLock(Long.valueOf(orderId.toString()));
            if (payOrder == null) {
                logger.info("callback fail payOrder is null, channelId:{},orderId:{}", channelId, orderId);
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
            }
            Game game = cacheManager.getGame(payOrder.getGameId());
            if (game == null) {
                logger.info("callback fail game is null, channelId:{},orderId:{}", channelId, orderId);
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
            }
            if (!Objects.equals(channel.getId(), payOrder.getChannelId())) {
                logger.info("callback fail channelId not match, channelId:{},orderId:{}", channelId, orderId);
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
            }

            String signConfig = channel.getCallbackSignConfig();
            if (StringUtils.isBlank(signConfig)) {
                logger.info("callback fail signConfig is null, channelId:{},orderId:{}", channelId, orderId);
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
            }
            // 处理回调签名
            JSONObject signJson = JSONObject.parseObject(signConfig);
            Map<String, Object> signMap = signJson.getInnerMap();
            StringBuilder signBuilder = new StringBuilder();
            for (Map.Entry<String, Object> entry : signMap.entrySet()) {
                String key = entry.getKey();
                Object value = reqDataJson.get(entry.getValue().toString());
                if (Objects.equals(NULL_KEY, key)) {
                    signBuilder.append(value == null ? StringUtils.EMPTY : value.toString());
                } else {
                    if (value == null) {
                        value = callbackJson.get(entry.getValue().toString());
                    }
                    signBuilder.append(key).append(value == null ? StringUtils.EMPTY : value.toString());
                }
            }
            String signSource = signBuilder.toString();
            logger.info("sign source:{}, orderId:{}", signSource, orderId);
            String sign = DigestUtils.md5Hex(signSource.getBytes(StandardCharsets.UTF_8));
            if (!Objects.equals(sign, reqDataJson.getString(callbackJson.getString(SDKUtils.SIGN_KEY)))) {
                logger.info("sign fail ,orderId:{}, channelId:{}", orderId, channelId);
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
            }

            if (payOrder.getPayState() == PayState.STATE_SUCCESS) {
                logger.info("order state is success, orderId:{}", payOrder.getId());
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_SUCCESS);
            }
            if (payOrder.getPayState() != PayState.STATE_PAYING) {
                logger.info("order state is fail, orderId:{}, state:{}", payOrder.getId(), payOrder.getPayState());
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
            }

            Object successKey = callbackJson.get(SDKUtils.SUCCESS_KEY);

            // 开始处理支付订单
            if (successKey != null) {
                //  根据回调参数判断是否处理该单
                String successValue = reqDataJson.getString(successKey.toString());
                if (!Objects.equals(callbackJson.getString(SDKUtils.SUCCESS_VALUE), successValue)) {
                    logger.info("channel pay fail, orderId:{}, channelId:{}", orderId, channelId);
                    return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
                }
            }
            String money = reqDataJson.getString(callbackJson.getString(DATA_KEY));
            Integer moneyInt = new BigDecimal(money).multiply(new BigDecimal(100)).intValue();
            if (!Objects.equals(payOrder.getMoney(), moneyInt)
                    && !Objects.equals(payOrder.getMoney(), Integer.valueOf(money))) {
                logger.info("money is not match, orderId:{}, channelId:{}", orderId, channelId);
                return callbackJson.getString(SDKUtils.ORDER_RESPONSE_FAIL);
            }
            payOrder.setPayState(PayState.STATE_HANDING);
            payOrder.setSdkOrderTime(new Date());
            payOrder.setChannelOrderId(reqDataJson.getString(callbackJson.getString(SDKUtils.CHANNEL_ORDER_ID)));
            payOrderService.savePayOrder(payOrder);
            logger.info("order callback success, orderId:{}", orderId);
            NotifyService.notify(payOrder, payOrderService, game);
            return callbackJson.getString(SDKUtils.ORDER_RESPONSE_SUCCESS);
        } catch (Exception e) {
            logger.error("form callback exception channelId:" + channelId + " req:"+builder.toString(), e);
            resp.setCode(CommonCode.SYSTEM_ERROR);
            return JSON.toJSONString(resp);
        }
    }

}
