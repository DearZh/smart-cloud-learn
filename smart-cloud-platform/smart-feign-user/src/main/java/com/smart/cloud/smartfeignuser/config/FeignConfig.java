package com.smart.cloud.smartfeignuser.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    /** 
     * @description: 此处设置Feign的日志级别，从而了解Feign中的http请求细节
     * @return: feign.Logger.Level
     * @author: Arnold.zhao 
     * @date: 2020/1/9 
     */ 
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
