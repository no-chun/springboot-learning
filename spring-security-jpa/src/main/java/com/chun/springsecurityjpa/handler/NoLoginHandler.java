package com.chun.springsecurityjpa.handler;

import com.chun.springsecurityjpa.model.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class NoLoginHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(400);
        PrintWriter out = response.getWriter();
        Result result = new Result(400, "尚未登录，请先登录");
        out.write(new ObjectMapper().writeValueAsString(result));
        out.flush();
        out.close();
    }
}
