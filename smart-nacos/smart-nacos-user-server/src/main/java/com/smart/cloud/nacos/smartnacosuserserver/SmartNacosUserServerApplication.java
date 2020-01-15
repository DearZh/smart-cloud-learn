package com.smart.cloud.nacos.smartnacosuserserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SmartNacosUserServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartNacosUserServerApplication.class, args);
    }

}
