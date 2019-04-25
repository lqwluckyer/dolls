package com.game.sdk.dolls.entity;

import javax.persistence.*;

/**
 * 游戏对象
 */
@Entity
@Table(name = "t_game")
public class Game extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //
    private String gameKey;

    //
    @Column(length = 1024)
    private String gameSecret;

    //RSA公钥
    @Column(length = 2048)
    private String gameRSAPubKey;

    //RSA密钥
    @Column(length = 2048)
    private String gameRSAPriKey;

    //游戏名称
    private String gameName;

    //游戏原名称
    private String originalName;

    // 其他配置 , JSON 格式
    @Column(length = 2048)
    private String otherConfig;

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
}
