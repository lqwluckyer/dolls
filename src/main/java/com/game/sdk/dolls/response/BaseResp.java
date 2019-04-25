package com.game.sdk.dolls.response;

import java.io.Serializable;

/**
 * Created by Administrator on 2018-12-07.
 */
public class BaseResp<T> implements Serializable{

    private static final long serialVersionUID = -8033834732435357626L;
    private Integer code;
    private String msg;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
