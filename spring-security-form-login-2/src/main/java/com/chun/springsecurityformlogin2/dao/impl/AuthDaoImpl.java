package com.chun.springsecurityformlogin2.dao.impl;

import com.chun.springsecurityformlogin2.dao.AuthDao;
import com.chun.springsecurityformlogin2.mapper.AuthMapper;
import com.chun.springsecurityformlogin2.mapper.UserMapper;
import com.chun.springsecurityformlogin2.model.Authority;
import com.chun.springsecurityformlogin2.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;

@Repository("AuthDao")
public class AuthDaoImpl implements AuthDao {
    private final JdbcTemplate jdbcTemplate;

    public AuthDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int addAuth(Authority authority) {
        String sql = "insert into authorities(username, authority) values (?,?)";
        Object[] args = {authority.getUsername(), authority.getAuthority()};
        int[] argType = {Types.VARCHAR, Types.VARCHAR};
        return jdbcTemplate.update(sql, args, argType);
    }

    @Override
    public int updateAuth(Authority authority) {
        String sql = "update authorities set authority = ? where username = ?";
        Object[] args = {authority.getAuthority(), authority.getUsername()};
        int[] argType = {Types.VARCHAR, Types.VARCHAR};
        return jdbcTemplate.update(sql, args, argType);
    }

    @Override
    public int deleteAuth(String username) {
        String sql = "delete from authorities where username = ?";
        Object[] args = {username};
        int[] argType = {Types.VARCHAR};
        return jdbcTemplate.update(sql, args, argType);
    }

    @Override
    public List<Authority> getAuth(String username) {
        String sql = "select * from authorities where username = ?";
        Object[] args = {username};
        int[] argTypes = {Types.VARCHAR};
        List<Authority> authorities = this.jdbcTemplate.query(sql, args, argTypes, new AuthMapper());
        return authorities;
    }
}
