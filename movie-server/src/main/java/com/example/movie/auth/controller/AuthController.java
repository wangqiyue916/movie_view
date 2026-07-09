package com.example.movie.auth.controller;

import com.example.movie.auth.dto.LoginRequest;
import com.example.movie.auth.dto.RegisterRequest;
import com.example.movie.auth.service.AuthService;
import com.example.movie.auth.vo.CurrentUserVO;
import com.example.movie.auth.vo.LoginResponse;
import com.example.movie.common.config.LoginUserContext;
import com.example.movie.common.response.ApiResponse;
import com.example.movie.user.entity.UserEntity;
import com.example.movie.user.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    public AuthController(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        LoginUserContext.clear();
        return ApiResponse.success();
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserVO> me() {
        if (!LoginUserContext.isLogin()) {
            return ApiResponse.error(401, "未登录");
        }
        UserEntity user = userMapper.selectById(LoginUserContext.getUserId());
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        List<String> roles = LoginUserContext.getRoles();
        CurrentUserVO vo = new CurrentUserVO(user.getId(), user.getUsername(),
                user.getNickname(), user.getAvatarUrl(), roles);
        return ApiResponse.success(vo);
    }
}

