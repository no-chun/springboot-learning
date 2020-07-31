package com.chun.jdbcdemo.dao.impl;

import com.chun.jdbcdemo.dao.UserDao;
import com.chun.jdbcdemo.mapper.UserMapper;
import com.chun.jdbcdemo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository("UserDao")
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int add(User user) {
        String sql = "insert into user(name,password) values(:name,:password)";
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(this.jdbcTemplate.getDataSource());
        return template.update(sql, new BeanPropertySqlParameterSource(user));
    }

    @Override
    public int update(User user) {
        String sql = "update user set name = ?, password = ? where id = ?";
        Object[] args = {user.getName(), user.getPassword(), user.getId()};
        int[] argTypes = {Types.VARCHAR, Types.VARCHAR, Types.BIGINT};
        return this.jdbcTemplate.update(sql, args, argTypes);
    }

    @Override
    public int deleteById(Long id) {
        String sql = "delete from user where id = ?";
        Object[] args = {id};
        int[] argTypes = {Types.BIGINT};
        return this.jdbcTemplate.update(sql, args, argTypes);
    }

    @Override
    public List<Map<String, Object>> queryUsersListMap() {
        String sql = "select * from user";
        return this.jdbcTemplate.queryForList(sql);
    }

    @Override
    public User queryUserById(Long id) {
        String sql = "select * from user where id = ?";
        Object[] args = {id};
        int[] argTypes = {Types.BIGINT};
        List<User> userList = this.jdbcTemplate.query(sql, args, argTypes, new UserMapper());
        if (userList.size() >= 1) {
            return userList.get(0);
        }else{
            return null;
        }
    }
}
