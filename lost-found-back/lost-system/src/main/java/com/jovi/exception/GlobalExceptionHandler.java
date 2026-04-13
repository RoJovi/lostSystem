package com.jovi.exception;

import com.jovi.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//全局异常处理器
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public Result HException(Exception e){
        log.error("有人在代码里下毒！",e);
        return Result.error("出错了/(ㄒoㄒ)/~~");
    }
}
