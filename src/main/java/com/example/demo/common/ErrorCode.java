package com.example.demo.common;

public enum ErrorCode {

    SUCCESS(200, "success"),

    DUPLICATE_REQUEST(409, "该请求已处理，请勿重复提交"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或 token 无效"),
    FORBIDDEN(403, "没有权限"),
    FORBIDDENUSER(403,"不是你的工单"),
    NOT_FOUND(404, "资源不存在"),
    SYSTEM_ERROR(500, "系统异常"),


    USERNAME_DUPLICATE(10001, "用户名已存在"),
    USER_NOT_FOUND(10002, "用户不存在"),
    PASSWORD_ERROR(10003, "密码错误"),
    TICKET_NOT_FOUND(10004,"工单不存在" ),
    ARTICLE_NOT_FOUND(10005, "知识库文章不存在"),
    INVALID_STATUS_TRANSITION(10006, "工单状态流转非法"),
    TICKET_STATUS_CHANGED(10007, "工单状态已变化，请刷新后重试");


    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
