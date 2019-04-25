package com.game.sdk.dolls.cache;

import com.game.sdk.dolls.entity.ChannelSDK;
import com.game.sdk.dolls.sdk.SDKInterface;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SDKCacheManager {

    private static Logger logger = LoggerFactory.getLogger(SDKCacheManager.class);
    private static final SDKCacheManager INSTANCE = new SDKCacheManager();
    private static final String GET_SDK_SCRIPT_KEY="get.sdk.script.key";

    private Map<Integer, SDKInterface> sdkCaches;

    private SDKCacheManager(){
        sdkCaches = new HashMap<Integer, SDKInterface>();
    }

    public static SDKCacheManager getInstance(){
        return INSTANCE;
    }

    /***
     * 获取指定渠道的ISDKScript的实例
     * @param channelSDK
     * @return
     */
    public SDKInterface getSDKInstance(ChannelSDK channelSDK){

        if(channelSDK == null){
            logger.info("SDKInterface channel is null");
            return  null;
        }
        SDKInterface SDKInterface = sdkCaches.get(channelSDK.getId());
        if(SDKInterface == null){
            synchronized (GET_SDK_SCRIPT_KEY){
                SDKInterface = sdkCaches.get(channelSDK.getId());
                if(SDKInterface == null){
                    try {
                        if(StringUtils.isBlank(channelSDK.getVerifyClass())){
                            logger.info("SDKInterface VerifyClass is null, sdkId:{}", channelSDK.getId());
                            return null;
                        }
                        SDKInterface = (SDKInterface)Class.forName(channelSDK.getVerifyClass()).newInstance();
                        sdkCaches.put(channelSDK.getId(), SDKInterface);
                        return SDKInterface;
                    } catch (InstantiationException e) {
                        logger.error("getSDKScript InstantiationException , sdkId:" + channelSDK.getId(), e);
                    } catch (IllegalAccessException e) {
                        logger.error("getSDKScript IllegalAccessException, sdkId:" +  channelSDK.getId(), e);
                    } catch (ClassNotFoundException e) {
                        logger.error("getSDKScript ClassNotFoundException, sdkId:" + channelSDK.getId(), e);
                    }
                }
            }
        }
        return SDKInterface;
    }

}
