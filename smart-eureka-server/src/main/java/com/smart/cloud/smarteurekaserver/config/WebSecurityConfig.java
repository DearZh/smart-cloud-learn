package com.smart.cloud.smarteurekaserver.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author Arnold.zhao <a href="mailto:Arnold_zhao@126.com"/>
 * @create 2019-12-18
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //默认情况下添加SpringSecurity依赖的应用每个请求都需要添加CSRF token才能访问，
        //Eureka客户端注册时只使用账号密码即可，并不会添加对应的csrf token，所以需要配置/eureka/**路径不需要CSRF token。

        http.csrf().ignoringAntMatchers("/eureka/**");
        super.configure(http);
    }

}
