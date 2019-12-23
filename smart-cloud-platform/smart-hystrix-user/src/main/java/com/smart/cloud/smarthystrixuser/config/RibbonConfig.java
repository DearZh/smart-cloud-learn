package com.smart.cloud.smarthystrixuser.config;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
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

    /** 
     * @description: 如果当前pom中没有添加（spring-boot-starter-actuator）.jar的引用，则需要注册该HystrixMetricsStreamServlet
     * @param: [] 
     * @return: org.springframework.boot.web.servlet.ServletRegistrationBean
     * @author: Arnold.zhao 
     * @date: 2019/12/23 
     */ 
    /*@Bean
    public ServletRegistrationBean getServlet(){
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/actuator/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }
    */
}
