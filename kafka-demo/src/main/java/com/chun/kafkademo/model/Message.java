package com.chun.kafkademo.model;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionID = 1L;

    private String from;

    private String msg;


    public Message(String from, String msg) {
        this.from = from;
        this.msg = msg;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from='" + from + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
