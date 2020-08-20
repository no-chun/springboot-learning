package com.chun.filterinterceptor.config;

import com.chun.filterinterceptor.filters.ParamsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<ParamsFilter> paramsFilter() {
        FilterRegistrationBean<ParamsFilter> filterRegistrationBean = new FilterRegistrationBean<ParamsFilter>();
        ParamsFilter paramsFilter = new ParamsFilter();
        filterRegistrationBean.setFilter(paramsFilter);
        filterRegistrationBean.setOrder(2);
        List<String> urlList = new ArrayList<>();
        urlList.add("/index/*");
        filterRegistrationBean.setUrlPatterns(urlList);
        return filterRegistrationBean;
    }
}
