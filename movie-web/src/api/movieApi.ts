import request from './request'

// ── Types ──

export interface MovieListItem {
  id: number
  title: string
  director: string | null
  actors: string | null
  genres: string | null
  releaseDate: string | null
  region: string | null
  language: string | null
  posterUrl: string | null
  avgTotalScore: number | null
  ratingCount: number | null
}

export interface MovieDetail {
  id: number
  title: string
  originalTitle: string | null
  director: string | null
  actors: string | null
  genres: string | null
  releaseDate: string | null
  region: string | null
  language: string | null
  durationMinutes: number | null
  synopsis: string | null
  posterUrl: string | null
  status: string
  viewCount: number
  ratingCount: number
  avgTotalScore: number | null
  avgStoryScore: number | null
  avgVisualScore: number | null
  avgActingScore: number | null
}

export interface RatingSummary {
  avgTotalScore: number | null
  avgStoryScore: number | null
  avgVisualScore: number | null
  avgActingScore: number | null
  ratingCount: number
}

export interface MyRating {
  id: number
  totalScore: number
  storyScore: number | null
  visualScore: number | null
  actingScore: number | null
  createdAt: string
}

export interface ShortCommentItem {
  id: number
  movieId: number
  userId: number
  userNickname: string
  userAvatarUrl: string | null
  userTotalScore: number | null
  content: string
  likeCount: number
  reportCount: number
  likedByMe: boolean
  createdAt: string
}

export interface PageData<T> {
  list: T[]
  page: number
  pageSize: number
  total: number
}

export interface MovieQuery {
  keyword?: string
  genre?: string
  releaseYear?: string
  releasePeriod?: string
  region?: string
  contentType?: string
  page?: number
  pageSize?: number
}

export interface RatingSubmitRequest {
  totalScore: number
  storyScore?: number
  visualScore?: number
  actingScore?: number
}

export interface MovieCreateRequest {
  title: string
  originalTitle?: string
  director?: string
  actors?: string
  genres?: string
  releaseDate?: string
  region?: string
  language?: string
  durationMinutes?: number
  synopsis?: string
  posterUrl?: string
}

export interface MovieUpdateRequest {
  title?: string
  originalTitle?: string
  director?: string
  actors?: string
  genres?: string
  releaseDate?: string
  region?: string
  language?: string
  durationMinutes?: number
  synopsis?: string
  posterUrl?: string
}

// ── API ──

export const movieApi = {
  pageMovies(params: MovieQuery) {
    return request.get('/movies', { params }) as Promise<PageData<MovieListItem>>
  },

  getMovieDetail(id: number): Promise<MovieDetail> {
    return request.get(`/movies/${id}`) as Promise<MovieDetail>
  },

  submitRating(movieId: number, data: RatingSubmitRequest): Promise<void> {
    return request.post(`/movies/${movieId}/ratings`, data) as Promise<void>
  },

  getRatingSummary(movieId: number): Promise<RatingSummary> {
    return request.get(`/movies/${movieId}/ratings/summary`) as Promise<RatingSummary>
  },

  getMyRating(movieId: number): Promise<MyRating | null> {
    return request.get(`/movies/${movieId}/ratings/me`) as Promise<MyRating | null>
  },

  listShortComments(movieId: number, page = 1, pageSize = 10): Promise<PageData<ShortCommentItem>> {
    return request.get(`/movies/${movieId}/short-comments`, { params: { page, pageSize } }) as Promise<PageData<ShortCommentItem>>
  },

  postShortComment(movieId: number, data: { content: string }): Promise<ShortCommentItem> {
    return request.post(`/movies/${movieId}/short-comments`, data) as Promise<ShortCommentItem>
  },

  likeComment(commentId: number): Promise<void> {
    return request.post(`/short-comments/${commentId}/like`) as Promise<void>
  },

  unlikeComment(commentId: number): Promise<void> {
    return request.delete(`/short-comments/${commentId}/like`) as Promise<void>
  },

  reportComment(commentId: number, reason: string): Promise<void> {
    return request.post(`/short-comments/${commentId}/report`, { reason }) as Promise<void>
  },

  createMovie(data: MovieCreateRequest): Promise<MovieListItem> {
    return request.post('/admin/movies', data) as Promise<MovieListItem>
  },

  updateMovie(id: number, data: MovieUpdateRequest): Promise<MovieListItem> {
    return request.put(`/admin/movies/${id}`, data) as Promise<MovieListItem>
  },

  deleteMovie(id: number): Promise<void> {
    return request.delete(`/admin/movies/${id}`) as Promise<void>
  },
}
