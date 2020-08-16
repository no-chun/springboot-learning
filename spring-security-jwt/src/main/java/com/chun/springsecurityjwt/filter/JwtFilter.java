package com.chun.springsecurityjwt.filter;

import com.chun.springsecurityjwt.model.Result;
import com.chun.springsecurityjwt.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class JwtFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String jwt = req.getHeader("authorization");
        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.replace("Bearer ", "");
        }
        try {
            Claims claims = JwtUtil.getClaimsFromToken(jwt);
            String username = claims.getSubject();
            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get("authorities"));
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(token);
            filterChain.doFilter(req, response);
        } catch (Exception e) {
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.setContentType("application/json;charset=utf-8");
            resp.setStatus(400);
            PrintWriter out = response.getWriter();
            out.write(new ObjectMapper().writeValueAsString(new Result(400, "Token error", null)));
            out.flush();
            out.close();
        }
    }
}

