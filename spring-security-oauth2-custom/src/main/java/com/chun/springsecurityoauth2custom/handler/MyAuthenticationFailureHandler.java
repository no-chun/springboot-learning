package com.chun.springsecurityoauth2custom.handler;

import com.chun.springsecurityoauth2custom.model.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper mapper;

    public MyAuthenticationFailureHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException e)
            throws IOException {
        LOGGER.warn("登录失败: " + e.getMessage());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType("application/json;charset=utf-8");
        Result result = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        response.getWriter().write(mapper.writeValueAsString(result));
    }
}
