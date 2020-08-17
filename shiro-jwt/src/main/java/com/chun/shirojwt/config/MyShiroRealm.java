package com.chun.shirojwt.config;

import com.chun.shirojwt.model.JWTToken;
import com.chun.shirojwt.model.SysPermission;
import com.chun.shirojwt.model.SysRole;
import com.chun.shirojwt.model.UserInfo;
import com.chun.shirojwt.service.UserInfoService;
import com.chun.shirojwt.utils.JwtUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;


import javax.annotation.Resource;


public class MyShiroRealm extends AuthorizingRealm {

    @Resource
    private UserInfoService userInfoService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("权限验证");
        String username = JwtUtil.getUsername(principalCollection.toString());
        UserInfo userInfo = userInfoService.findByUsername(username);
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        for (SysRole role : userInfo.getRoleList()) {
            authorizationInfo.addRole(role.getRole());
            for (SysPermission permission : role.getPermissions()) {
                authorizationInfo.addStringPermission(permission.getPermission());
            }
        }
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("Token验证");
        String token = (String) authenticationToken.getCredentials();
        String username = JwtUtil.getUsername(token);
        if (username == null) {
            throw new AuthenticationException("token invalid");
        }
        UserInfo userInfo = userInfoService.findByUsername(username);
        if (userInfo == null) {
            throw new AuthenticationException("User didn't existed!");
        }
        if (!JwtUtil.verify(token, userInfo.getUsername(), userInfo.getPassword())) {
            throw new AuthenticationException("Username or password error");
        }
        System.out.println("验证成功");
        return new SimpleAuthenticationInfo(token, token, getName());
    }
}
