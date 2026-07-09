import { defineStore } from 'pinia'
import { authApi, type CurrentUser, type LoginRequest } from '@/api/authApi'

interface UserState {
  token: string
  userInfo: CurrentUser | null
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: localStorage.getItem('token') || '',
    userInfo: null,
  }),
  getters: {
    isLogin: (state) => Boolean(state.token),
    roles: (state) => state.userInfo?.roles || [],
  },
  actions: {
    async login(data: LoginRequest) {
      const result = await authApi.login(data)
      this.token = result.token
      this.userInfo = result.user
      localStorage.setItem('token', result.token)
    },
    async fetchCurrentUser() {
      this.userInfo = await authApi.me()
    },
    hasRole(role: string) {
      return this.roles.includes(role)
    },
    hasAnyRole(roles: string[]) {
      if (!roles.length) return true
      return roles.some((role) => this.roles.includes(role))
    },
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
    },
  },
})

