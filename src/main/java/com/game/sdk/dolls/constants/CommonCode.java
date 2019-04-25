package com.game.sdk.dolls.constants;

/**
 * Created by Administrator on 2018-10-11.
 */
public class CommonCode {
    private CommonCode() {
    }

    public static final int NUMBER0 = 0;
    public static final int NUMBER1 = 1;
    public static final int NUMBER2 = 2;
    public static final int NUMBER3 = 3;
    public static final int NUMBER4 = 4;
    public static final int NUMBER5 = 5;
    public static final int NUMBER6 = 6;
    public static final int NUMBER7 = 7;
    public static final int NUMBER8 = 8;
    public static final int NUMBER9 = 9;

    public static final int SUCCESS = NUMBER0;
    public static final int SYSTEM_ERROR = NUMBER1; // 系统异常
    public static final int GAME_NONE = NUMBER2;
    public static final int CHANNEL_NONE = NUMBER3;
    public static final int SDK_NONE = NUMBER4;
    public static final int LOGIN_FAILED = NUMBER5;
    public static final int USER_NONE = NUMBER6;
    public static final int VERIFY_FAILED = NUMBER7;
    public static final int TOKEN_ERROR = NUMBER8;
    public static final int ORDER_ERROR = NUMBER9;
    public static final int SIGN_ERROR = 11;
    public static final int PAY_CLOSED = 12;           //充值未开放
    public static final int CHANNEL_NOT_MATCH = 13;    //渠道和游戏没有对上
    public static final int ARGS_ERROR = 14;    //渠道和游戏没有对上
    public static final int LOGIN_CLOSED = 15;    //登陆入口关闭
    public static final int REGISTER_CLOSED = 16;    //注册入口关闭
}
