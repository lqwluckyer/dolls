package com.game.sdk.dolls.bean;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext(){

        return applicationContext;
    }

    public static Object getBean(String name){

        return applicationContext.getBean(name);
    }

    public static Object getBean(String name, Class type){
        return applicationContext.getBean(name, type);
    }

}
