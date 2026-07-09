package com.example.movie.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.movie.common.exception.BusinessException;
import com.example.movie.common.exception.ErrorCode;
import com.example.movie.common.response.PageResult;
import com.example.movie.movie.dto.ShortCommentSubmitRequest;
import com.example.movie.movie.entity.LikeEntity;
import com.example.movie.movie.entity.MovieRatingEntity;
import com.example.movie.movie.entity.ReportEntity;
import com.example.movie.movie.entity.ShortCommentEntity;
import com.example.movie.movie.mapper.LikeMapper;
import com.example.movie.movie.mapper.MovieRatingMapper;
import com.example.movie.movie.mapper.ReportMapper;
import com.example.movie.movie.mapper.ShortCommentMapper;
import com.example.movie.movie.service.ShortCommentService;
import com.example.movie.movie.vo.ShortCommentVO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShortCommentServiceImpl implements ShortCommentService {

    private final ShortCommentMapper commentMapper;
    private final LikeMapper likeMapper;
    private final MovieRatingMapper movieRatingMapper;
    private final ReportMapper reportMapper;
    private final JdbcTemplate jdbcTemplate;

    public ShortCommentServiceImpl(ShortCommentMapper commentMapper, LikeMapper likeMapper,
                                   MovieRatingMapper movieRatingMapper,
                                   ReportMapper reportMapper, JdbcTemplate jdbcTemplate) {
        this.commentMapper = commentMapper;
        this.likeMapper = likeMapper;
        this.movieRatingMapper = movieRatingMapper;
        this.reportMapper = reportMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PageResult<ShortCommentVO> listComments(Long movieId, int page, int pageSize, Long currentUserId) {
        Page<ShortCommentEntity> p = new Page<>(page, pageSize);
        QueryWrapper<ShortCommentEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("movie_id", movieId).eq("status", "NORMAL").orderByDesc("created_at");
        IPage<ShortCommentEntity> result = commentMapper.selectPage(p, wrapper);

        List<ShortCommentEntity> entities = result.getRecords();
        if (entities.isEmpty()) {
            return PageResult.empty(page, pageSize);
        }

        Set<Long> userIds = entities.stream().map(ShortCommentEntity::getUserId).collect(Collectors.toSet());

        // Batch fetch user info
        Map<Long, Map<String, Object>> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            String inClause = userIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            List<Map<String, Object>> users = jdbcTemplate.queryForList(
                    "SELECT id, nickname, avatar_url FROM users WHERE id IN (" + inClause + ")");
            for (Map<String, Object> u : users) {
                userMap.put((Long) u.get("id"), u);
            }
        }

        Map<Long, MovieRatingEntity> ratingMap = new HashMap<>();
        List<MovieRatingEntity> ratings = movieRatingMapper.selectList(
                new LambdaQueryWrapper<MovieRatingEntity>()
                        .eq(MovieRatingEntity::getMovieId, movieId)
                        .in(MovieRatingEntity::getUserId, userIds));
        for (MovieRatingEntity rating : ratings) {
            ratingMap.put(rating.getUserId(), rating);
        }

        // Batch check likes
        Set<Long> likedIds = new HashSet<>();
        if (currentUserId != null) {
            List<Long> commentIds = entities.stream().map(ShortCommentEntity::getId).collect(Collectors.toList());
            String idList = commentIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            likedIds = new HashSet<>(likeMapper.findLikedTargetIds(currentUserId, "SHORT_COMMENT", idList));
        }

        List<ShortCommentVO> list = new ArrayList<>();
        for (ShortCommentEntity e : entities) {
            Map<String, Object> user = userMap.getOrDefault(e.getUserId(), Map.of());
            list.add(new ShortCommentVO(
                    e.getId(), e.getMovieId(), e.getUserId(),
                    (String) user.getOrDefault("nickname", "用户"),
                    (String) user.getOrDefault("avatar_url", null),
                    ratingMap.containsKey(e.getUserId()) ? ratingMap.get(e.getUserId()).getTotalScore() : null,
                    e.getContent(), e.getLikeCount(), e.getReportCount(),
                    likedIds.contains(e.getId()), e.getCreatedAt()));
        }

        return new PageResult<>(list, page, pageSize, result.getTotal());
    }

    @Override
    public ShortCommentVO postComment(Long movieId, Long userId, ShortCommentSubmitRequest request) {
        MovieRatingEntity rating = movieRatingMapper.selectOne(
                new LambdaQueryWrapper<MovieRatingEntity>()
                        .eq(MovieRatingEntity::getMovieId, movieId)
                        .eq(MovieRatingEntity::getUserId, userId));
        if (rating == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "请先完成电影评分后再发布短评");
        }

        ShortCommentEntity entity = new ShortCommentEntity();
        entity.setMovieId(movieId);
        entity.setUserId(userId);
        entity.setContent(request.content());
        entity.setStatus("NORMAL");
        entity.setLikeCount(0);
        entity.setReportCount(0);
        commentMapper.insert(entity);

        return new ShortCommentVO(
                entity.getId(), movieId, userId, "", null,
                rating.getTotalScore(), request.content(), 0, 0, false, entity.getCreatedAt());
    }

    @Override
    @Transactional
    public void likeComment(Long commentId, Long userId) {
        LikeEntity existing = likeMapper.selectOne(
                new LambdaQueryWrapper<LikeEntity>()
                        .eq(LikeEntity::getUserId, userId)
                        .eq(LikeEntity::getTargetType, "SHORT_COMMENT")
                        .eq(LikeEntity::getTargetId, commentId));
        if (existing != null) {
            throw new BusinessException(ErrorCode.CONFLICT, "请勿重复点赞");
        }

        LikeEntity like = new LikeEntity();
        like.setUserId(userId);
        like.setTargetType("SHORT_COMMENT");
        like.setTargetId(commentId);
        likeMapper.insert(like);

        ShortCommentEntity comment = commentMapper.selectById(commentId);
        if (comment != null) {
            ShortCommentEntity update = new ShortCommentEntity();
            update.setId(commentId);
            update.setLikeCount(comment.getLikeCount() + 1);
            commentMapper.updateById(update);
        }
    }

    @Override
    @Transactional
    public void unlikeComment(Long commentId, Long userId) {
        LambdaQueryWrapper<LikeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeEntity::getUserId, userId)
               .eq(LikeEntity::getTargetType, "SHORT_COMMENT")
               .eq(LikeEntity::getTargetId, commentId);
        int deleted = likeMapper.delete(wrapper);
        if (deleted == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "尚未点赞，无法取消");
        }

        ShortCommentEntity comment = commentMapper.selectById(commentId);
        if (comment != null && comment.getLikeCount() > 0) {
            ShortCommentEntity update = new ShortCommentEntity();
            update.setId(commentId);
            update.setLikeCount(comment.getLikeCount() - 1);
            commentMapper.updateById(update);
        }
    }

    @Override
    public void reportComment(Long commentId, Long userId, String reason) {
        ReportEntity report = new ReportEntity();
        report.setReporterId(userId);
        report.setTargetType("SHORT_COMMENT");
        report.setTargetId(commentId);
        report.setReason(reason);
        report.setStatus("PENDING");
        reportMapper.insert(report);

        ShortCommentEntity comment = commentMapper.selectById(commentId);
        if (comment != null) {
            ShortCommentEntity update = new ShortCommentEntity();
            update.setId(commentId);
            update.setReportCount(comment.getReportCount() + 1);
            commentMapper.updateById(update);
        }
    }
}
