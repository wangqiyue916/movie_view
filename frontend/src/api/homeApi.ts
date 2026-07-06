import request from './request'

export const homeApi = {
  getHomeData() {
    return request.get<unknown, Record<string, unknown>>('/home')
  },
}

