package com.chun.springsecurityoauth2custom.handler;

import com.chun.springsecurityoauth2custom.model.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final ClientDetailsService clientDetailsService;

    private final AuthorizationServerTokenServices authorizationServerTokenServices;
    private final ObjectMapper mapper;

    public MyAuthenticationSuccessHandler(ClientDetailsService clientDetailsService,
                                          @Qualifier("defaultAuthorizationServerTokenServices")
                                                  AuthorizationServerTokenServices
                                                  authorizationServerTokenServices,
                                          ObjectMapper mapper) {
        this.clientDetailsService = clientDetailsService;
        this.authorizationServerTokenServices = authorizationServerTokenServices;
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic ")) {
            throw new UnapprovedClientAuthenticationException("请求头中无client信息");
        }
        String[] token = getToken(header);
        String clientId = token[0];
        String clientSecret = token[1];
        TokenRequest tokenRequest;
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        if (clientDetails == null) {
            throw new UnapprovedClientAuthenticationException("clientId:" + clientId + "对应的信息不存在");
        } else if (!StringUtils.equals(clientDetails.getClientSecret(), clientSecret)) {
            throw new UnapprovedClientAuthenticationException("clientSecret不正确");
        } else {
            tokenRequest = new TokenRequest(new HashMap<>(), clientId, clientDetails.getScope(), "custom");
        }
        OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
        OAuth2Authentication auth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
        OAuth2AccessToken tokens = authorizationServerTokenServices.createAccessToken(auth2Authentication);
        LOGGER.info("登录成功");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(mapper.writeValueAsString(new Result(200, "Success", tokens)));
    }

    private String[] getToken(String header) {
        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Failed to decode basic authentication token");
        }
        String token = new String(decoded, StandardCharsets.UTF_8);
        int splitIndex = token.indexOf(":");
        if (splitIndex == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        } else {
            return new String[]{token.substring(0, splitIndex), token.substring(splitIndex + 1)};
        }
    }
}
