package com.game.sdk.dolls.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018-12-05.
 */
public class EasyUIResp<T extends Serializable> implements Serializable{

    private static final long serialVersionUID = 3114524908694407329L;
    private Integer code;
    private String msg;
    private Long total;
    private List<T> rows;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
