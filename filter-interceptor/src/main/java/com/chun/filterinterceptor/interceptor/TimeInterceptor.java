package com.chun.filterinterceptor.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class TimeInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOGGER.info(this.getClass() + "拦截之前");
        LOGGER.info(((HandlerMethod) handler).getMethod().getName());
        request.setAttribute("startTime", new Date().getTime());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LOGGER.info(this.getClass() + "开始处理拦截");
        long start = (long) request.getAttribute("startTime");
        LOGGER.info(this.getClass() + "耗时" + (new Date().getTime() - start));
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LOGGER.info(this.getClass() + "处理拦截后");
        long start = (long) request.getAttribute("startTime");
        LOGGER.info(this.getClass() + "耗时" + (new Date().getTime() - start));
        assert ex != null;
        LOGGER.info("异常信息：" + ex.getMessage());
    }
}
