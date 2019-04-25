package com.game.sdk.dolls.vo;

import java.io.Serializable;

public class SimpleGameVo implements Serializable {
    private static final long serialVersionUID = 7039933370939573547L;
    private Integer id;

    //游戏名称
    private String gameName;

    //游戏原名称
    private String originalName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
}
