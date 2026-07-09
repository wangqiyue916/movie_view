package com.example.movie.movie.controller;

import com.example.movie.common.config.LoginUserContext;
import com.example.movie.common.exception.BusinessException;
import com.example.movie.common.exception.ErrorCode;
import com.example.movie.common.response.ApiResponse;
import com.example.movie.common.response.PageResult;
import com.example.movie.movie.dto.ShortCommentSubmitRequest;
import com.example.movie.movie.service.ShortCommentService;
import com.example.movie.movie.vo.ShortCommentVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies/{movieId}/short-comments")
public class ShortCommentController {

    private final ShortCommentService commentService;

    public ShortCommentController(ShortCommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ApiResponse<PageResult<ShortCommentVO>> listComments(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long currentUserId = LoginUserContext.getUserId();
        return ApiResponse.success(commentService.listComments(movieId, page, pageSize, currentUserId));
    }

    @PostMapping
    public ApiResponse<ShortCommentVO> postComment(@PathVariable Long movieId,
                                                    @Valid @RequestBody ShortCommentSubmitRequest request) {
        Long userId = LoginUserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        return ApiResponse.success(commentService.postComment(movieId, userId, request));
    }
}
