package com.example.movie.movie.service;

import com.example.movie.common.response.PageResult;
import com.example.movie.movie.dto.ShortCommentSubmitRequest;
import com.example.movie.movie.vo.ShortCommentVO;

public interface ShortCommentService {

    PageResult<ShortCommentVO> listComments(Long movieId, int page, int pageSize, Long currentUserId);

    ShortCommentVO postComment(Long movieId, Long userId, ShortCommentSubmitRequest request);

    void likeComment(Long commentId, Long userId);

    void unlikeComment(Long commentId, Long userId);

    void reportComment(Long commentId, Long userId, String reason);
}
