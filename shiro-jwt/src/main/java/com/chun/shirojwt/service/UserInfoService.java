package com.chun.shirojwt.service;

import com.chun.shirojwt.model.UserInfo;

public interface UserInfoService {
    UserInfo findByUsername(String username);
}
