package com.smart.cloud.smartuserservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
/**
 * Cloud  Edgware 后的版本，无需显示指定也可以被注册中心发现 test success（https://blog.csdn.net/zheng199172/article/details/82466139）
 */
@EnableDiscoveryClient
public class SmartUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartUserServiceApplication.class, args);
    }

}
