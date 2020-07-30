package com.chun.mybatis.mapper;

import com.chun.mybatis.enums.UserSexEnum;
import com.chun.mybatis.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;


public interface UserMapper {
    @Select("SELECT * FROM USER")
    @Results({
            @Result(property = "userSex", column = "USER_SEX", javaType = UserSexEnum.class),
            @Result(property = "nickName", column = "NICK_NAME")
    })
    List<User> getAll();

    @Select("SELECT * FROM USER WHERE ID = #{id}")
    @Results({
            @Result(property = "userSex", column = "USER_SEX", javaType = UserSexEnum.class),
            @Result(property = "nickName", column = "NICK_NAME")
    })
    User getOne(Long id);

    @Insert("INSERT INTO USER(USERNAME, PASSWORD, USER_SEX) VALUES (#{userName}, #{passWord}, #{userSex})")
    void insert(User user);

    @Update("UPDATE USER SET USERNAME=#{userName},NICK_NAME=#{nickName} WHERE ID =#{id}")
    void update(User user);

    @Delete("DELETE FROM USER WHERE ID =#{id}")
    void delete(Long id);
}
