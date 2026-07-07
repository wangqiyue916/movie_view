package com.example.movie.auth.vo;

public record LoginResponse(
        String token,
        CurrentUserVO user
) {
}

