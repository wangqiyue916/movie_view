package com.example.movie.movie.controller;

import com.example.movie.common.response.ApiResponse;
import com.example.movie.common.response.PageResult;
import com.example.movie.movie.dto.MovieQueryRequest;
import com.example.movie.movie.service.MovieService;
import com.example.movie.movie.vo.MovieDetailVO;
import com.example.movie.movie.vo.MovieListVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ApiResponse<PageResult<MovieListVO>> listMovies(MovieQueryRequest query) {
        return ApiResponse.success(movieService.searchMovies(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<MovieDetailVO> getMovie(@PathVariable Long id) {
        return ApiResponse.success(movieService.getMovieDetail(id));
    }
}
