package com.chun.springsecurityformlogin2.dao.impl;

import com.chun.springsecurityformlogin2.dao.UserDao;
import com.chun.springsecurityformlogin2.mapper.UserMapper;
import com.chun.springsecurityformlogin2.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;

@Repository("UserDao")
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int add(User user) {
        String sql = "insert into users(username,password,enabled) values (?,?,?)";
        Object[] args = {user.getUsername(), user.getPassword(), user.isEnabled()};
        int[] argType = {Types.VARCHAR, Types.VARCHAR, Types.SMALLINT};
        return jdbcTemplate.update(sql, args, argType);
    }

    @Override
    public int update(User user) {
        String sql = "update users set password = ?, enabled = ? where username = ?";
        Object[] args = {user.getPassword(), user.isEnabled(), user.getUsername()};
        int[] argType = {Types.VARCHAR, Types.SMALLINT, Types.VARCHAR};
        return jdbcTemplate.update(sql, args, argType);
    }

    @Override
    public int deleteUserByName(String username) {
        String sql = "delete from users where username = ?";
        Object[] args = {username};
        int[] argType = {Types.VARCHAR};
        return jdbcTemplate.update(sql, args, argType);
    }

    @Override
    public User findByName(String username) {
        String sql = "select * from users where username = ?";
        Object[] args = {username};
        int[] argTypes = {Types.VARCHAR};
        List<User> userList = this.jdbcTemplate.query(sql, args, argTypes, new UserMapper());
        if (userList.size() >= 1) {
            return userList.get(0);
        } else {
            return null;
        }
    }
}
