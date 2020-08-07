package com.chun.shirodemo.service.impl;

import com.chun.shirodemo.model.UserInfo;
import com.chun.shirodemo.repository.UserInfoRepository;
import com.chun.shirodemo.service.UserInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Resource
    private UserInfoRepository userInfoRepository;


    @Override
    public UserInfo findByUsername(String username) {
        return userInfoRepository.findByUsername(username);
    }
}
