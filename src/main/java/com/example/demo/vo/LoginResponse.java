package com.example.demo.vo;

public class LoginResponse {
    private String token;
    private String username;
    private long userId;
    private String role;
    public LoginResponse() {

    }
    public LoginResponse( String token, String username, long userId,String role) {
        this.token = token;
        this.username = username;
        this.userId = userId;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
