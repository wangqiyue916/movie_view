package com.example.movie.movie.service;

import com.example.movie.common.response.PageResult;
import com.example.movie.movie.dto.MovieCreateRequest;
import com.example.movie.movie.dto.MovieQueryRequest;
import com.example.movie.movie.dto.MovieUpdateRequest;
import com.example.movie.movie.entity.MovieEntity;
import com.example.movie.movie.vo.MovieDetailVO;
import com.example.movie.movie.vo.MovieListVO;

public interface MovieService {

    PageResult<MovieListVO> searchMovies(MovieQueryRequest query);

    MovieDetailVO getMovieDetail(Long movieId);

    MovieEntity createMovie(MovieCreateRequest request);

    MovieEntity updateMovie(Long movieId, MovieUpdateRequest request);

    void deleteMovie(Long movieId);
}
