import { defineStore } from 'pinia'
import type { AiChatMessage } from '@/api/aiApi'

export const useAiStore = defineStore('ai', {
  state: () => ({
    visible: false,
    sessionId: null as number | null,
    messages: [] as AiChatMessage[],
    loading: false,
  }),
  getters: {
    hasSession: (state) => state.sessionId !== null,
  },
  actions: {
    toggle() {
      this.visible = !this.visible
    },
    open() {
      this.visible = true
    },
    close() {
      this.visible = false
    },
    addMessage(msg: AiChatMessage) {
      this.messages.push(msg)
    },
    setLoading(v: boolean) {
      this.loading = v
    },
    clearMessages() {
      this.messages = []
    },
  },
})
