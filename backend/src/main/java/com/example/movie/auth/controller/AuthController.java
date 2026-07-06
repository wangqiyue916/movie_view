package com.example.movie.auth.controller;

import com.example.movie.auth.dto.LoginRequest;
import com.example.movie.auth.dto.RegisterRequest;
import com.example.movie.auth.vo.CurrentUserVO;
import com.example.movie.auth.vo.LoginResponse;
import com.example.movie.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        CurrentUserVO user = new CurrentUserVO(1L, request.username(), request.nickname(), null, List.of("USER"));
        return ApiResponse.success(new LoginResponse("dev-token", user));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        List<String> roles = switch (request.username()) {
            case "admin" -> List.of("ADMIN");
            case "super_admin" -> List.of("SUPER_ADMIN");
            case "official" -> List.of("OFFICIAL");
            default -> List.of("USER");
        };
        CurrentUserVO user = new CurrentUserVO(1L, request.username(), request.username(), null, roles);
        return ApiResponse.success(new LoginResponse("dev-token", user));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success();
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserVO> me() {
        return ApiResponse.success(new CurrentUserVO(1L, "dev_user", "开发用户", null, List.of("USER")));
    }
}

