package com.example.movie.movie.controller;

import com.example.movie.common.config.LoginUserContext;
import com.example.movie.common.exception.BusinessException;
import com.example.movie.common.exception.ErrorCode;
import com.example.movie.common.response.ApiResponse;
import com.example.movie.movie.service.ShortCommentService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/short-comments")
public class ShortCommentInteractionController {

    private final ShortCommentService commentService;

    public ShortCommentInteractionController(ShortCommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{commentId}/like")
    public ApiResponse<Void> like(@PathVariable Long commentId) {
        Long userId = LoginUserContext.getUserId();
        if (userId == null) throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        commentService.likeComment(commentId, userId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{commentId}/like")
    public ApiResponse<Void> unlike(@PathVariable Long commentId) {
        Long userId = LoginUserContext.getUserId();
        if (userId == null) throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        commentService.unlikeComment(commentId, userId);
        return ApiResponse.success();
    }

    @PostMapping("/{commentId}/report")
    public ApiResponse<Void> report(@PathVariable Long commentId, @RequestBody Map<String, String> body) {
        Long userId = LoginUserContext.getUserId();
        if (userId == null) throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        String reason = body.get("reason");
        if (reason == null || reason.isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "举报原因不能为空");
        }
        commentService.reportComment(commentId, userId, reason);
        return ApiResponse.success();
    }
}
