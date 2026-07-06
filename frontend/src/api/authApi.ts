import request from './request'

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  nickname: string
}

export interface CurrentUser {
  id: number
  username: string
  nickname: string
  avatarUrl?: string
  roles: string[]
}

export interface LoginResponse {
  token: string
  user: CurrentUser
}

export const authApi = {
  login(data: LoginRequest) {
    return request.post<unknown, LoginResponse>('/auth/login', data)
  },
  register(data: RegisterRequest) {
    return request.post<unknown, LoginResponse>('/auth/register', data)
  },
  logout() {
    return request.post<unknown, void>('/auth/logout')
  },
  me() {
    return request.get<unknown, CurrentUser>('/auth/me')
  },
}

