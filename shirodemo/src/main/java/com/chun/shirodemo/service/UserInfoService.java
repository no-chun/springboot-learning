package com.chun.shirodemo.service;

import com.chun.shirodemo.model.UserInfo;

public interface UserInfoService {
    UserInfo findByUsername(String username);
}
