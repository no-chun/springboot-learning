package com.chun.mybatismult.model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long versionId = 1L;

    private Long id;
    private String userName;
    private String passWord;
    private String email;
    private String nickName;
    private String regTime;


    public User() {
    }

    public User(Long id, String userName, String passWord, String email, String nickName, String regTime) {
        this.id = id;
        this.userName = userName;
        this.passWord = passWord;
        this.email = email;
        this.nickName = nickName;
        this.regTime = regTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    @Override
    public String toString() {
        return userName + " " + email + " " + nickName + " " + regTime;
    }
}
