package com.chun.json.model;

import com.chun.json.util.InfoDeserializer;
import com.chun.json.util.InfoSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

@JsonSerialize(using = InfoSerializer.class)
@JsonDeserialize(using = InfoDeserializer.class)
public class Info {
    private String msg;

    private Date time;

    public Info(String msg, Date time) {
        this.msg = msg;
        this.time = time;
    }

    public Info() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
