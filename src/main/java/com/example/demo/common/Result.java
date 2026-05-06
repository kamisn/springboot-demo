package com.example.demo.common;

public class Result <T>{
    private Integer code;
    private String message;
    private T data;
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public Result() {
    }
    public static <T>  Result<T> success(T data){
        return new Result<>(ErrorCode.SUCCESS.getCode(),ErrorCode.SUCCESS.getMessage(),data);
    }
    public static <T> Result<T> success() {
        return new Result<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), null);
    }

    public static <T> Result<T> fail(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null);
    }//用来应对ErrorCode中已经有的异常信息

    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }//完全自定义的异常信息，code message都是自己写
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }




    public void setData(T data) {
        this.data = data;
    }
}
