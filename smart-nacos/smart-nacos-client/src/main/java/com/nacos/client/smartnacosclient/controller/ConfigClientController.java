package com.nacos.client.smartnacosclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Arnold.zhao <a href="mailto:Arnold_zhao@126.com"/>
 * @create 2020-01-13
 */
@RestController
/**
 *支持动态刷新该Bean
 */
@RefreshScope
public class ConfigClientController {

    @Value("${arnold.zhao}")
    private String serverName;

    @GetMapping("/configInfo")
    public String getConfigInfo() {
        return serverName;
    }
}
