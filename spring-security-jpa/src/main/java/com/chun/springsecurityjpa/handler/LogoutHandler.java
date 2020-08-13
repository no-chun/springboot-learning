package com.chun.springsecurityjpa.handler;

import com.chun.springsecurityjpa.model.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class LogoutHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(200);
        PrintWriter out = response.getWriter();
        Result result = new Result(200, "logout success！");
        out.write(new ObjectMapper().writeValueAsString(result));
        out.flush();
        out.close();
    }
}
