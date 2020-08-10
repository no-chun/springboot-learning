package com.chun.controlleradvice.handler;

import com.chun.controlleradvice.exception.CustomException;
import com.chun.controlleradvice.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        logger.error(e.getMessage());
        return new Result(500, e.getMessage());
    }

    @ExceptionHandler(CustomException.class)
    public Result handlerCustomeException(CustomException e) {
        logger.error(e.getMessage());
        return new Result(500, e.getMsg());
    }
}
