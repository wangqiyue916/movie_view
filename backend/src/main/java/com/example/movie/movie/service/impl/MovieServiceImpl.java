package com.example.movie.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.movie.common.exception.BusinessException;
import com.example.movie.common.exception.ErrorCode;
import com.example.movie.common.response.PageResult;
import com.example.movie.movie.dto.MovieCreateRequest;
import com.example.movie.movie.dto.MovieQueryRequest;
import com.example.movie.movie.dto.MovieUpdateRequest;
import com.example.movie.movie.entity.MovieEntity;
import com.example.movie.movie.mapper.MovieMapper;
import com.example.movie.movie.service.MovieService;
import com.example.movie.movie.vo.MovieDetailVO;
import com.example.movie.movie.vo.MovieListVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieMapper movieMapper;

    public MovieServiceImpl(MovieMapper movieMapper) {
        this.movieMapper = movieMapper;
    }

    @Override
    public PageResult<MovieListVO> searchMovies(MovieQueryRequest query) {
        QueryWrapper<MovieEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "ONLINE");

        if (query.keyword() != null && !query.keyword().isBlank()) {
            String kw = query.keyword().trim();
            wrapper.and(w -> w
                    .like("title", kw)
                    .or().like("director", kw)
                    .or().like("actors", kw));
        }
        if (query.genre() != null && !query.genre().isBlank()) {
            wrapper.like("genres", query.genre().trim());
        }
        if (query.releaseYear() != null && !query.releaseYear().isBlank()) {
            wrapper.apply("YEAR(release_date) = {0}", query.releaseYear().trim());
        } else if (query.releasePeriod() != null && !query.releasePeriod().isBlank()) {
            applyReleasePeriod(wrapper, query.releasePeriod().trim());
        }
        if (query.region() != null && !query.region().isBlank()) {
            applyRegion(wrapper, query.region().trim());
        }
        if (query.contentType() != null && !query.contentType().isBlank()
                && !"电影".equals(query.contentType().trim())) {
            wrapper.eq("id", -1);
        }

        wrapper.orderByDesc("avg_total_score", "created_at");

        Page<MovieEntity> page = new Page<>(query.page(), query.pageSize());
        Page<MovieEntity> result = movieMapper.selectPage(page, wrapper);

        List<MovieListVO> list = result.getRecords().stream()
                .map(m -> new MovieListVO(
                        m.getId(), m.getTitle(), m.getDirector(), m.getActors(),
                        m.getGenres(), m.getReleaseDate(), m.getRegion(), m.getLanguage(), m.getPosterUrl(),
                        m.getAvgTotalScore(), m.getRatingCount()))
                .collect(Collectors.toList());

        return new PageResult<>(list, query.page(), query.pageSize(), result.getTotal());
    }

    @Override
    public MovieDetailVO getMovieDetail(Long movieId) {
        MovieEntity m = movieMapper.selectOne(
                new QueryWrapper<MovieEntity>().eq("status", "ONLINE").eq("id", movieId));
        if (m == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "电影不存在或已删除");
        }

        MovieEntity update = new MovieEntity();
        update.setId(movieId);
        update.setViewCount(m.getViewCount() + 1);
        movieMapper.updateById(update);

        return new MovieDetailVO(
                m.getId(), m.getTitle(), m.getOriginalTitle(), m.getDirector(),
                m.getActors(), m.getGenres(), m.getReleaseDate(), m.getRegion(),
                m.getLanguage(), m.getDurationMinutes(), m.getSynopsis(),
                m.getPosterUrl(), m.getStatus(), m.getViewCount() + 1,
                m.getRatingCount(), m.getAvgTotalScore(), m.getAvgStoryScore(),
                m.getAvgVisualScore(), m.getAvgActingScore());
    }

    @Override
    public MovieEntity createMovie(MovieCreateRequest request) {
        MovieEntity entity = new MovieEntity();
        entity.setTitle(request.title());
        entity.setOriginalTitle(request.originalTitle());
        entity.setDirector(request.director());
        entity.setActors(request.actors());
        entity.setGenres(request.genres());
        entity.setReleaseDate(request.releaseDate());
        entity.setRegion(request.region());
        entity.setLanguage(request.language());
        entity.setDurationMinutes(request.durationMinutes());
        entity.setSynopsis(request.synopsis());
        entity.setPosterUrl(request.posterUrl());
        entity.setStatus("ONLINE");
        entity.setViewCount(0L);
        entity.setRatingCount(0);
        movieMapper.insert(entity);
        return entity;
    }

    @Override
    public MovieEntity updateMovie(Long movieId, MovieUpdateRequest request) {
        MovieEntity entity = movieMapper.selectById(movieId);
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "电影不存在");
        }
        if (request.title() != null) entity.setTitle(request.title());
        if (request.originalTitle() != null) entity.setOriginalTitle(request.originalTitle());
        if (request.director() != null) entity.setDirector(request.director());
        if (request.actors() != null) entity.setActors(request.actors());
        if (request.genres() != null) entity.setGenres(request.genres());
        if (request.releaseDate() != null) entity.setReleaseDate(request.releaseDate());
        if (request.region() != null) entity.setRegion(request.region());
        if (request.language() != null) entity.setLanguage(request.language());
        if (request.durationMinutes() != null) entity.setDurationMinutes(request.durationMinutes());
        if (request.synopsis() != null) entity.setSynopsis(request.synopsis());
        if (request.posterUrl() != null) entity.setPosterUrl(request.posterUrl());
        movieMapper.updateById(entity);
        return entity;
    }

    @Override
    public void deleteMovie(Long movieId) {
        MovieEntity entity = movieMapper.selectById(movieId);
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "电影不存在");
        }
        movieMapper.deleteById(movieId);
    }

    private void applyReleasePeriod(QueryWrapper<MovieEntity> wrapper, String period) {
        switch (period) {
            case "2026-2020" -> wrapper.apply("YEAR(release_date) BETWEEN {0} AND {1}", 2020, 2026);
            case "2019-2010" -> wrapper.apply("YEAR(release_date) BETWEEN {0} AND {1}", 2010, 2019);
            case "2009-2000" -> wrapper.apply("YEAR(release_date) BETWEEN {0} AND {1}", 2000, 2009);
            case "90年代" -> wrapper.apply("YEAR(release_date) BETWEEN {0} AND {1}", 1990, 1999);
            case "80年代" -> wrapper.apply("YEAR(release_date) BETWEEN {0} AND {1}", 1980, 1989);
            case "更早" -> wrapper.apply("YEAR(release_date) <= {0}", 1979);
            default -> {
            }
        }
    }

    private void applyRegion(QueryWrapper<MovieEntity> wrapper, String region) {
        switch (region) {
            case "中国" -> wrapper.and(w -> w.like("region", "中国")
                    .or().like("region", "大陆")
                    .or().like("region", "香港")
                    .or().like("region", "台湾"));
            case "美国" -> wrapper.like("region", "美国");
            case "日韩" -> wrapper.and(w -> w.like("region", "日本")
                    .or().like("region", "韩国")
                    .or().like("region", "日韩"));
            case "欧洲" -> wrapper.and(w -> w.like("region", "英国")
                    .or().like("region", "法国")
                    .or().like("region", "德国")
                    .or().like("region", "意大利")
                    .or().like("region", "西班牙")
                    .or().like("region", "欧洲"));
            default -> wrapper.like("region", region);
        }
    }
}
