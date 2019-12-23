package com.smart.cloud.smarthystrixuser.filter;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * 使用Hystrix的请求缓存时必须对HystrixRequestContext进行初始化
 *
 * @author Arnold.zhao <a href="mailto:Arnold_zhao@126.com"/>
 * @create 2019-12-18
 */
@Component
@WebFilter(urlPatterns = "/*", asyncSupported = true)
public class HystrixRequestContextFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //HystrixRequestContext context = HystrixRequestContext.initializeContext();
    }

    /**
     * 使用Hystrix的请求缓存时必须对HystrixRequestContext进行初始化，否则将提示异常
     * <textarea>java.lang.IllegalStateException: Request caching is not available. Maybe you need to initialize the HystrixRequestContext?</textarea>
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HystrixRequestContext context = HystrixRequestContext.initializeContext();

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            context.close();
        }

    }

    @Override
    public void destroy() {

    }
}
