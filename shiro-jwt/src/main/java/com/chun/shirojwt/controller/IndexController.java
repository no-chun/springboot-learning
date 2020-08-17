package com.chun.shirojwt.controller;

import com.chun.shirojwt.model.Result;
import com.chun.shirojwt.model.UserInfo;
import com.chun.shirojwt.service.UserInfoService;
import com.chun.shirojwt.utils.JwtUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpServletResponse;

@RestController
public class IndexController {

    private final UserInfoService userInfoService;

    public IndexController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @RequestMapping("/403")
    public Result unauthorizedRole(HttpServletResponse response) {
        response.setStatus(403);
        return new Result(403, "unauthorized", null);
    }

    @PostMapping("/login")
    public Result login(@RequestParam("username") String username,
                        @RequestParam("password") String password) {
        UserInfo userInfo = userInfoService.findByUsername(username);
        password = new Md5Hash(password, userInfo.getCredentialsSalt(), 2).toString();
        if (userInfo.getPassword().equals(password)) {
            return new Result(200, "ok", JwtUtil.sign(username, password));
        } else {
            return new Result(400, "username or password error", null);
        }
    }

    @RequestMapping("/index")
    @RequiresAuthentication
    public Result index() {
        return new Result(200, "ok", "index");
    }

    @RequestMapping("/admin")
    @RequiresRoles("admin")
    public Result admin() {
        return new Result(200, "ok", "admin!");
    }

    @RequestMapping("/add")
    @RequiresPermissions("userInfo:add")
    public Result add() {
        return new Result(200, "ok", "add!");
    }
}
