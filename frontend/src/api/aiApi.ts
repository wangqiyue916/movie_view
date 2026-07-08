import request from './request'

export interface AiChatMessage {
  id: number
  sessionId: number
  role: 'USER' | 'ASSISTANT'
  content: string
  relatedType: string | null
  relatedId: number | null
  createdAt: string
}

export interface AiChatSession {
  id: number
  userId: number | null
  title: string
  createdAt: string
}

export const aiApi = {
  createSession() {
    return request.post<unknown, AiChatSession>('/ai/sessions')
  },
  getMessages(sessionId: number) {
    return request.get<unknown, AiChatMessage[]>(`/ai/sessions/${sessionId}/messages`)
  },
  sendMessage(sessionId: number, content: string) {
    return request.post<unknown, AiChatMessage>(`/ai/sessions/${sessionId}/chat`, { content })
  },
  deleteSession(sessionId: number) {
    return request.delete<unknown, void>(`/ai/sessions/${sessionId}`)
  }
}
