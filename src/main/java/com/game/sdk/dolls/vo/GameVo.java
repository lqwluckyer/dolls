package com.game.sdk.dolls.vo;

import java.io.Serializable;
import java.util.Date;

public class GameVo implements Serializable {

    private static final long serialVersionUID = -6357077750320672274L;
    private Integer id;

    //游戏名称
    private String gameName;

    //游戏原名称
    private String originalName;

    //
    private String gameKey;

    //
    private String gameSecret;

    //RSA公钥
    private String gameRSAPubKey;

    //RSA密钥
    private String gameRSAPriKey;

    // 其他配置 , JSON 格式
    private String otherConfig;

    private Date createTime;

    private Date editTime;

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

    public String getGameKey() {
        return gameKey;
    }

    public void setGameKey(String gameKey) {
        this.gameKey = gameKey;
    }

    public String getGameSecret() {
        return gameSecret;
    }

    public void setGameSecret(String gameSecret) {
        this.gameSecret = gameSecret;
    }

    public String getGameRSAPubKey() {
        return gameRSAPubKey;
    }

    public void setGameRSAPubKey(String gameRSAPubKey) {
        this.gameRSAPubKey = gameRSAPubKey;
    }

    public String getGameRSAPriKey() {
        return gameRSAPriKey;
    }

    public void setGameRSAPriKey(String gameRSAPriKey) {
        this.gameRSAPriKey = gameRSAPriKey;
    }

    public String getOtherConfig() {
        return otherConfig;
    }

    public void setOtherConfig(String otherConfig) {
        this.otherConfig = otherConfig;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }
}
