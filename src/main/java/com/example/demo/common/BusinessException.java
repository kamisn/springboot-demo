package com.example.demo.common;

public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();//这里用的是ErrorCode里的getcode方法
    }//自定义报错message

    public Integer getCode() {
        return code;//这个get方法是给异常对象用的，
    }
}