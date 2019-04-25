package com.game.sdk.dolls;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Created by Administrator on 2019-01-21.
 */
@SpringBootApplication
public class LuckyApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(LuckyApplication.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LuckyApplication.class, args);
    }
}
