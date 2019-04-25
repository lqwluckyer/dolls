package com.game.sdk.dolls.controller.admin;

import com.game.sdk.dolls.request.payorder.SearchPayOrderReq;
import com.game.sdk.dolls.response.EasyUIResp;
import com.game.sdk.dolls.service.PayOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/payOrder")
public class PayOrderManagerController {

    @Autowired
    private PayOrderService payOrderService;

    @RequestMapping(value = "/showList", method = RequestMethod.GET)
    public String showList(){
        return "payOrderList";
    }

    @RequestMapping(value = "/getPayOrderList", method = RequestMethod.POST)
    @ResponseBody
    public EasyUIResp getPayOrderList(SearchPayOrderReq req){

        return payOrderService.getPayOrderList(req);
    }
}
