package com.game.sdk.dolls.service;


import com.game.sdk.dolls.entity.PayOrder;
import com.game.sdk.dolls.request.payorder.PayOrderReq;
import com.game.sdk.dolls.request.payorder.SearchPayOrderReq;
import com.game.sdk.dolls.response.BaseResp;
import com.game.sdk.dolls.response.EasyUIResp;

public interface PayOrderService {
    BaseResp getOrderInfo(PayOrderReq req);
    PayOrder getPayOrderWithLock(Long id);
    void savePayOrder(PayOrder payOrder);
    EasyUIResp getPayOrderList(SearchPayOrderReq req);
}
