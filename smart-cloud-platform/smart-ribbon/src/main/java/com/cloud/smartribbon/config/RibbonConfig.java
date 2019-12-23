package com.cloud.smartribbon.config;

import com.netflix.loadbalancer.BestAvailableRule;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Arnold.zhao <a href="mailto:Arnold_zhao@126.com"/>
 * @create 2019-12-18
 */
@Configuration
public class RibbonConfig {


    /**
     * @description: @LoadBalanced定义当前具备负载均衡的RestTemplate能力
     * @return: org.springframework.web.client.RestTemplate
     * @author: Arnold.zhao
     * @date: 2019/12/18
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * @description: 定义Ribbon的负载均衡策略（RandomRule：实例随机选择）
     * @return: com.netflix.loadbalancer.IRule
     * @author: Arnold.zhao
     * @date: 2019/12/18
     */
    @Bean
    public IRule ribbonRule() {
        return new RandomRule();
    }
}
