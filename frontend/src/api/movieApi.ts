import request from './request'

export interface MovieQuery {
  keyword?: string
  genre?: string
  releaseYear?: string
  page?: number
  pageSize?: number
}

export const movieApi = {
  pageMovies(params: MovieQuery) {
    return request.get<unknown, Record<string, unknown>>('/movies', { params })
  },
  getMovieDetail(movieId: number | string) {
    return request.get<unknown, Record<string, unknown>>(`/movies/${movieId}`)
  },
}

