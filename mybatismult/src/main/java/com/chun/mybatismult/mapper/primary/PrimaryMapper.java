package com.chun.mybatismult.mapper.primary;

import com.chun.mybatismult.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface PrimaryMapper {
    @Select("SELECT * FROM user")
    @Results({
            @Result(property = "userName", column = "userName"),
            @Result(property = "email", column = "email")
    })
    List<User> getAll();

    @Select("SELECT * FROM user WHERE id = #{id}")
    @Results({
            @Result(property = "userName", column = "userName"),
            @Result(property = "email", column = "email")
    })
    User getOne(Long id);

    @Insert("INSERT INTO user(userName, passWord, email, nickName, regTime)" +
            " VALUES (#{userName}, #{passWord}, #{email}, #{nickName}, #{regTime})")
    void insert(User user);

    @Update("UPDATE user SET userName = #{userName}, passWord = #{passWord}," +
            " email = #{email}, nickName = #{nickName}, regTime = #{regTime}" +
            "WHERE id = #{id}")
    void update(User user);

    @Delete("DELETE FROM user WHERE id =#{id}")
    void delete(Long id);
}