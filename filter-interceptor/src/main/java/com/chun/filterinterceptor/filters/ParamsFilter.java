package com.chun.filterinterceptor.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

public class ParamsFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParamsFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("Filter 2 启动！");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        LOGGER.info("执行Filter 2中......");
        servletRequest.setAttribute("name", "chun");
        servletRequest.setAttribute("pwd", "xxxx");
        String type = (String) servletRequest.getParameter("type");
        LOGGER.info(type);
        filterChain.doFilter(servletRequest, servletResponse);
        servletResponse.setContentType("application/json");
        LOGGER.info("Filter 2结束");
    }

    @Override
    public void destroy() {
        LOGGER.error("Filter 2 销毁了！");
    }
}
