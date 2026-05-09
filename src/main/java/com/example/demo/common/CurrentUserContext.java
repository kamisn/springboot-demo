package com.example.demo.common;

public class CurrentUserContext {

    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE_HOLDER = new ThreadLocal<>();
    //ThreadLocal 可以把当前用户 id 临时绑定到当前线程里，这样 TicketService 里就能拿到当前登录用户。
    public static void set(Long userId, String username,String role) {
        USER_ID_HOLDER.set(userId);
        USERNAME_HOLDER.set(username);
        ROLE_HOLDER.set(role);
    }

    public static Long getUserId() {
        return USER_ID_HOLDER.get();
    }
    public static String getRole() {
        return ROLE_HOLDER.get();
    }

    public static String getUsername() {
        return USERNAME_HOLDER.get();
    }

    public static void clear() {
        USER_ID_HOLDER.remove();
        USERNAME_HOLDER.remove();
        ROLE_HOLDER.remove();
    }
}