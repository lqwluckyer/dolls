package com.game.sdk.dolls.controller;

import com.game.sdk.dolls.request.payorder.PayOrderReq;
import com.game.sdk.dolls.response.BaseResp;
import com.game.sdk.dolls.service.PayOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payOrder")
public class PayOrderController {

    @Autowired
    private PayOrderService payOrderService;

    @RequestMapping(value = "/getOrderInfo", method = {RequestMethod.POST})
    public BaseResp getOrderInfo(PayOrderReq req){

        return payOrderService.getOrderInfo(req);
    }
}
