package com.example.movie.auth.vo;

import java.util.List;

public record CurrentUserVO(
        Long id,
        String username,
        String nickname,
        String avatarUrl,
        List<String> roles
) {
}

