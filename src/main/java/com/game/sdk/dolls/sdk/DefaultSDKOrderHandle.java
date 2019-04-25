package com.game.sdk.dolls.sdk;

import com.alibaba.fastjson.JSONObject;
import com.game.sdk.dolls.constants.CommonCode;
import com.game.sdk.dolls.entity.Channel;
import com.game.sdk.dolls.entity.ChannelSDK;
import com.game.sdk.dolls.entity.PayOrder;
import com.game.sdk.dolls.entity.Player;
import com.game.sdk.dolls.response.BaseResp;
import com.game.sdk.dolls.vo.OrderInfoVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.game.sdk.dolls.utils.SDKUtils.ORDER_SIGN_TYPE;


@Service("DefaultSDKOrderHandle")
public class DefaultSDKOrderHandle implements SDKOrderHandle {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSDKOrderHandle.class);

    @Override
    public BaseResp handleChannelOrder(PayOrder payOrder, Player player, Channel channel, ChannelSDK sdk) {
        BaseResp resp = new BaseResp();
        String config = channel.getParametersConfig();
        if(StringUtils.isBlank(config)){
            logger.info("channel config is null, orderId:{}, playerId:{}", payOrder.getId(), player.getId());
            resp.setCode(CommonCode.ORDER_ERROR);
            return resp;
        }

        JSONObject configJson = JSONObject.parseObject(config);
        Object type = configJson.get(ORDER_SIGN_TYPE);
        if(type != null){
            // 只返回订单号、回调地址、签名，客户端下单，需要服务端返回签名
            return resp;
        }else {
            //  默认情况 只返回订单号和回调地址 , 客户端下单，不需要服务端签名
            OrderInfoVo data = new OrderInfoVo();
            data.setOrderId(payOrder.getId());
            data.setCallback(sdk.getPayCallbackUrl()+channel.getId());
            JSONObject ext = new JSONObject();
            data.setExtension(ext.toJSONString());
            resp.setCode(CommonCode.SUCCESS);
            resp.setData(data);
            return resp;
        }
    }
}
