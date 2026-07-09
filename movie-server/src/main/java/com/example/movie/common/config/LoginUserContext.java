package com.example.movie.common.config;

import java.util.List;

public class LoginUserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> ROLES = new ThreadLocal<>();

    public static void setUserId(Long userId) { USER_ID.set(userId); }
    public static Long getUserId() { return USER_ID.get(); }

    public static void setUsername(String username) { USERNAME.set(username); }
    public static String getUsername() { return USERNAME.get(); }

    @SuppressWarnings("unchecked")
    public static void setRoles(List<String> roles) { ROLES.set(roles); }
    public static List<String> getRoles() { return ROLES.get(); }

    public static boolean hasRole(String role) {
        List<String> roles = getRoles();
        return roles != null && roles.contains(role);
    }

    public static boolean isLogin() {
        return getUserId() != null;
    }

    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
        ROLES.remove();
    }
}