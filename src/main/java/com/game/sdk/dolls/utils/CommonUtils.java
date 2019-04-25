package com.game.sdk.dolls.utils;

import org.apache.commons.lang3.StringUtils;

public class CommonUtils {
    private CommonUtils(){}

    public static String null2emptyString(Object obj){
        if(obj==null){
            return StringUtils.EMPTY;
        }
        return obj.toString();
    }
}
