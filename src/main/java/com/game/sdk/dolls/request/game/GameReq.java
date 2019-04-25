package com.game.sdk.dolls.request.game;

public class GameReq {
    private Integer id;

    //游戏名称
    private String gameName;

    //游戏原名称
    private String originalName;
    private String gameKey;
    private String gameSecret;
    private String gameRSAPubKey;

    //RSA密钥
    private String gameRSAPriKey;

    // 其他配置 , JSON 格式
    private String otherConfig;

    //当前请求的页码
    private Integer page;
    //当前每页显示的行数
    private Integer rows;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getOtherConfig() {
        return otherConfig;
    }

    public void setOtherConfig(String otherConfig) {
        this.otherConfig = otherConfig;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }
}
