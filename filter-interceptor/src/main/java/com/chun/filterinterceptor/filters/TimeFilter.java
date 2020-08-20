package com.chun.filterinterceptor.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.Date;

@Component
@Order(1)
@WebFilter(urlPatterns = {"/index/*"})
public class TimeFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("Filter 1 启动！");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        LOGGER.info("---------------------------------------");
        LOGGER.info("执行Filter 1中......");
        long start = new Date().getTime();
        filterChain.doFilter(servletRequest, servletResponse);
        LOGGER.info("Filter 1耗时：" + ((new Date().getTime()) - start));
        LOGGER.info("Filter 1结束");
        LOGGER.info("---------------------------------------");
    }

    @Override
    public void destroy() {
        LOGGER.error("Filter 1 销毁了！");
    }
}
