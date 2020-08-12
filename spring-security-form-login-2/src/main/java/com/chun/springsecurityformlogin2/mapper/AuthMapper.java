package com.chun.springsecurityformlogin2.mapper;

import com.chun.springsecurityformlogin2.model.Authority;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthMapper implements RowMapper<Authority> {
    @Override
    public Authority mapRow(ResultSet resultSet, int i) throws SQLException {
        Authority authority = new Authority();
        authority.setUsername(resultSet.getString("username"));
        authority.setAuthority(resultSet.getString("authority"));
        return authority;
    }
}
