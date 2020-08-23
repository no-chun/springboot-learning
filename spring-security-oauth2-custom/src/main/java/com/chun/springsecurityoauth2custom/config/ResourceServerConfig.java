package com.chun.springsecurityoauth2custom.config;

import com.chun.springsecurityoauth2custom.handler.MyAuthenticationFailureHandler;
import com.chun.springsecurityoauth2custom.handler.MyAuthenticationSuccessHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private final MyAuthenticationSuccessHandler authenticationSuccessHandler;

    private final MyAuthenticationFailureHandler authenticationFailureHandler;

    public ResourceServerConfig(MyAuthenticationSuccessHandler authenticationSuccessHandler,
                                MyAuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginProcessingUrl("/login")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .csrf().disable();
    }

}
