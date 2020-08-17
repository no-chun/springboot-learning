package com.chun.shirojwt.service.impl;

import com.chun.shirojwt.model.UserInfo;
import com.chun.shirojwt.repository.UserInfoRepository;
import com.chun.shirojwt.service.UserInfoService;
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
