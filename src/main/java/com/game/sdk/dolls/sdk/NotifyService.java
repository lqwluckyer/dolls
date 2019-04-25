package com.game.sdk.dolls.sdk;


import com.alibaba.fastjson.JSONObject;
import com.game.sdk.dolls.constants.PayState;
import com.game.sdk.dolls.entity.Game;
import com.game.sdk.dolls.entity.PayOrder;
import com.game.sdk.dolls.service.PayOrderService;
import com.game.sdk.dolls.utils.HttpUtil;
import com.game.sdk.dolls.utils.RSAUtils;
import com.game.sdk.dolls.utils.SDKUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Objects;

import static com.game.sdk.dolls.utils.SDKUtils.NOTIFY_SUCCESS;

public class NotifyService {

    private static final Logger logger = LoggerFactory.getLogger(NotifyService.class);

    public static void notify(PayOrder payOrder, PayOrderService payOrderService, Game game){
        try{
            if (payOrder == null) {
                logger.info("order is null");
            }
            logger.info("start notify order orderId:{}", payOrder.getId());
            if(payOrder.getPayState() == PayState.STATE_SUCCESS){
                logger.info("order state is success, orderId:{}", payOrder.getId());
                return;
            }
            if (payOrder.getPayState()!=PayState.STATE_HANDING) {
                logger.info("order state is fail, orderId:{}, state:{}", payOrder.getId(), payOrder.getPayState());
                return;
            }
            String signSource = SDKUtils.getNotifySign(payOrder, game);
            logger.info("notify order signSource:{}", signSource);
            String sign = RSAUtils.sign(game.getGameRSAPriKey(), signSource);
            JSONObject reqData = new JSONObject();
            reqData.put("appId", game.getId());
            reqData.put("channelId", payOrder.getChannelId());
            reqData.put("currency", payOrder.getCurrency());
            reqData.put("extension", payOrder.getExtension());
            reqData.put("money", payOrder.getMoney());
            reqData.put("orderId", payOrder.getId());
            reqData.put("playerId", payOrder.getPlayerId());
            reqData.put("productId", payOrder.getProductId());
            reqData.put("productName", payOrder.getProductName());
            reqData.put("productDesc", payOrder.getProductDesc());
            reqData.put("roleId", payOrder.getRoleId());
            reqData.put("roleName", payOrder.getRoleName());
            reqData.put("serverId", payOrder.getServerId());
            reqData.put("serverName", payOrder.getServerName());
            reqData.put("userName", payOrder.getUserName());
            reqData.put("sign", sign);
            String req = reqData.toJSONString();
            logger.info("notify order reqData:{}, notifyUrl:{}", req, payOrder.getNotifyUrl());
            String result = HttpUtil.sendPostJson(payOrder.getNotifyUrl(),req);
            if(StringUtils.isBlank(result)){
                logger.info("notify order fail, response is null, req:{}",req);
                payOrder.setPayState(PayState.STATE_FAILED);
                payOrder.setCpResp("cp resp is null");
                payOrderService.savePayOrder(payOrder);
                return;
            }
            if(Objects.equals(NOTIFY_SUCCESS, result)){
                logger.info("notify order success, req:{}, response:{}",req, result);
                payOrder.setPayState(PayState.STATE_SUCCESS);
                payOrder.setCpResp(result);
                payOrder.setCompleteTime(new Date());
                payOrderService.savePayOrder(payOrder);
                return;
            }
            logger.info("notify order fail, req:{}, response:{}",req, result);
            payOrder.setPayState(PayState.STATE_FAILED);
            payOrder.setCpResp(result);
            payOrderService.savePayOrder(payOrder);
        }catch (Exception e){
            payOrder.setPayState(PayState.STATE_FAILED);
            payOrder.setCpResp("system exception");
            payOrderService.savePayOrder(payOrder);
            logger.error("notify cp exception, orderId:" + payOrder.getId(), e);
        }

    }

}
