package com.game.sdk.dolls.constants;

/**
 * 支付状态
 */
public class PayState {
    private PayState(){}

    //支付中状态。游戏服务器下单之后，订单状态支付中
    public static final int STATE_PAYING = CommonCode.NUMBER0;

    //支付回调处理成功状态,但尚未通知游戏服务器发货
    public static final int STATE_HANDING =  CommonCode.NUMBER1;

    //订单完成状态。通知游戏发货成功状态
    public static final int STATE_SUCCESS =  CommonCode.NUMBER2;

    // 支付回调处理失败状态
    public static final int STATE_FAILED =  CommonCode.NUMBER3;

}
