package com.chun.controlleradvice.handler;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InitHandler {
    @InitBinder("user")
    public void init(WebDataBinder dataBinder){
        dataBinder.setFieldDefaultPrefix("user.");
    }
}
