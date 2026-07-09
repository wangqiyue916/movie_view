package com.example.movie.common.config;

import com.example.movie.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            LoginUserContext.clear();
            return true;
        }

        try {
            String token = authHeader.substring(7);
            Map<String, Object> claims = JwtUtil.parseToken(token);
            if (JwtUtil.isTokenExpired(claims)) {
                LoginUserContext.clear();
                return true;
            }
            LoginUserContext.setUserId(JwtUtil.getUserId(claims));
            LoginUserContext.setUsername(JwtUtil.getUsername(claims));
            LoginUserContext.setRoles(JwtUtil.getRoles(claims));
        } catch (Exception e) {
            LoginUserContext.clear();
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        LoginUserContext.clear();
    }
}