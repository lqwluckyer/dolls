package com.game.sdk.dolls.sdk;

/**
 * SDK 验证结果
 */
public class SDKLoginResult {

    private boolean success;
    private String userId;
    private String userName;
    private String nickName;
    private String extension;

    public SDKLoginResult(boolean success, String userId, String userName, String nickName){
        this.success = success;
        this.userId = userId;
        this.userName = userName;
        this.nickName = nickName;
    }


    public SDKLoginResult(boolean success, String userId, String userName, String nickName, String extension){
        this.success = success;
        this.userId = userId;
        this.userName = userName;
        this.nickName = nickName;
        this.extension = extension;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
