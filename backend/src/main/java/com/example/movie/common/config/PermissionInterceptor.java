package com.example.movie.common.config;

import com.example.movie.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class PermissionInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod hm)) {
            return true;
        }

        // Check @RequireRole
        RequireRole requireRole = hm.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = hm.getBeanType().getAnnotation(RequireRole.class);
        }
        if (requireRole != null) {
            if (!LoginUserContext.isLogin()) {
                writeJson(response, 401, "请先登录");
                return false;
            }
            List<String> userRoles = LoginUserContext.getRoles();
            boolean hasRole = false;
            for (String required : requireRole.value()) {
                if (userRoles != null && userRoles.contains(required)) {
                    hasRole = true;
                    break;
                }
            }
            if (!hasRole) {
                writeJson(response, 403, "无权限访问");
                return false;
            }
        }

        // Check @RequirePermission is optional - just check if user has any of required permissions
        // For phase 3, we only implement role-based access control
        RequirePermission requirePermission = hm.getMethodAnnotation(RequirePermission.class);
        if (requirePermission == null) {
            requirePermission = hm.getBeanType().getAnnotation(RequirePermission.class);
        }
        if (requirePermission != null) {
            if (!LoginUserContext.isLogin()) {
                writeJson(response, 401, "请先登录");
                return false;
            }
            // Permission check placeholder - full implementation in phase 4/5
        }

        return true;
    }

    private void writeJson(HttpServletResponse response, int code, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(code);
        ApiResponse<Void> body = ApiResponse.error(code, message);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}