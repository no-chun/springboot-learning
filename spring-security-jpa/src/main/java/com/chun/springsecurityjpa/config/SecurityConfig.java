package com.chun.springsecurityjpa.config;

import com.chun.springsecurityjpa.handler.LoginFailureHandler;
import com.chun.springsecurityjpa.handler.LoginSuccessHandler;
import com.chun.springsecurityjpa.handler.NoLoginHandler;
import com.chun.springsecurityjpa.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final NoLoginHandler noLoginHandler;

    public SecurityConfig(UserService userService,
                          LoginSuccessHandler loginSuccessHandler,
                          LoginFailureHandler loginFailureHandler,
                          LogoutSuccessHandler logoutSuccessHandler,
                          NoLoginHandler noLoginHandler) {
        this.userService = userService;
        this.loginSuccessHandler = loginSuccessHandler;
        this.loginFailureHandler = loginFailureHandler;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.noLoginHandler = noLoginHandler;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_admin > ROLE_user");
        return hierarchy;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("admin")
                .antMatchers("/user/**").hasRole("user")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginProcessingUrl("/login")
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler)
                .permitAll()
                .and()
                .csrf().disable().exceptionHandling()
                .authenticationEntryPoint(noLoginHandler);
    }
}