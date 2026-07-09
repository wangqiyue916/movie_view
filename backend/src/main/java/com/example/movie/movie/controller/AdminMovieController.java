package com.example.movie.movie.controller;

import com.example.movie.common.response.ApiResponse;
import com.example.movie.movie.dto.MovieCreateRequest;
import com.example.movie.movie.dto.MovieUpdateRequest;
import com.example.movie.movie.entity.MovieEntity;
import com.example.movie.movie.service.MovieService;
import com.example.movie.movie.vo.MovieListVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin/movies")
public class AdminMovieController {

    private final MovieService movieService;

    public AdminMovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    public ApiResponse<MovieListVO> createMovie(@Valid @RequestBody MovieCreateRequest request) {
        MovieEntity entity = movieService.createMovie(request);
        return ApiResponse.success(new MovieListVO(
                entity.getId(), entity.getTitle(), entity.getDirector(),
                entity.getActors(), entity.getGenres(), entity.getReleaseDate(),
                entity.getRegion(), entity.getLanguage(), entity.getPosterUrl(), BigDecimal.ZERO, 0));
    }

    @PutMapping("/{id}")
    public ApiResponse<MovieListVO> updateMovie(@PathVariable Long id, @RequestBody MovieUpdateRequest request) {
        MovieEntity entity = movieService.updateMovie(id, request);
        return ApiResponse.success(new MovieListVO(
                entity.getId(), entity.getTitle(), entity.getDirector(),
                entity.getActors(), entity.getGenres(), entity.getReleaseDate(),
                entity.getRegion(), entity.getLanguage(), entity.getPosterUrl(),
                entity.getAvgTotalScore(), entity.getRatingCount()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ApiResponse.success();
    }
}
