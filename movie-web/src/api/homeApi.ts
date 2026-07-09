import request from './request'

export interface NewsArticle {
  id: number
  title: string
  summary: string
  content: string
  coverUrl: string
  category: string
  source: string
  status: string
  viewCount: number
  isHot: number
  publishedAt: string
  createdAt: string
}

export interface NewsRelation {
  id: number
  newsId: number
  targetType: string
  targetId: number
  targetName: string
}

export interface HomeData {
  bannerNews: NewsArticle[]
  bannerRecs: RecCard[]
  latestNews: NewsArticle[]
  hotMovies: MovieItem[]
  topRatedMovies: MovieItem[]
  latestMovies: MovieItem[]
  featuredReviews: ReviewItem[]
  recommendedMerchandise: MerchItem[]
}

export interface RecCard {
  id: number
  title: string
  image: string
  targetType: string
}

export interface MovieItem {
  id: number
  title: string
  poster: string
  score: string
}

export interface ReviewItem {
  id?: number
  title: string
  excerpt: string
  author: string
  date: string
  likes: number
  comments: number
}

export interface MerchItem {
  id: number
  name: string
  image: string
  price: string
}

export interface PageResult<T> {
  list: T[]
  page: number
  pageSize: number
  total: number
}

export const homeApi = {
  getHomeData() {
    return request.get<unknown, HomeData>('/home')
  },

  getNewsList(params: {
    page?: number
    pageSize?: number
    keyword?: string
    category?: string
    movieId?: number
  }) {
    return request.get<unknown, PageResult<NewsArticle>>('/news', { params })
  },

  getNewsDetail(newsId: number) {
    return request.get<unknown, NewsArticle>(`/news/${newsId}`)
  },

  getNewsRelations(newsId: number) {
    return request.get<unknown, NewsRelation[]>(`/news/${newsId}/relations`)
  },

  toggleLike(newsId: number, userId = 1) {
    return request.post<unknown, { liked: boolean; likeCount: number }>(`/news/${newsId}/like`, null, { params: { userId } })
  },

  getLikeStatus(newsId: number, userId = 1) {
    return request.get<unknown, { likeCount: number; isLiked: boolean }>(`/news/${newsId}/like-status`, { params: { userId } })
  },

  toggleFavorite(newsId: number, userId = 1) {
    return request.post<unknown, { favorited: boolean }>(`/news/${newsId}/favorite`, null, { params: { userId } })
  },

  getFavoriteStatus(newsId: number, userId = 1) {
    return request.get<unknown, { isFavorited: boolean }>(`/news/${newsId}/favorite-status`, { params: { userId } })
  },
}
