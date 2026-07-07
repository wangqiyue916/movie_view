package com.example.movie.auth.service;

import com.example.movie.auth.dto.LoginRequest;
import com.example.movie.auth.dto.RegisterRequest;
import com.example.movie.auth.vo.LoginResponse;

public interface AuthService {

    LoginResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}