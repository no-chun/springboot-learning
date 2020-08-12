package com.chun.springsecurityformlogin2.dao;

import com.chun.springsecurityformlogin2.model.Authority;

import java.util.List;

public interface AuthDao {
    int addAuth(Authority authority);

    int updateAuth(Authority authority);

    int deleteAuth(String username);

    List<Authority> getAuth(String username);
}
