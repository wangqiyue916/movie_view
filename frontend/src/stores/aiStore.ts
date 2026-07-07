import { defineStore } from 'pinia'

export const useAiStore = defineStore('ai', {
  state: () => ({
    visible: false,
    sessionId: null as number | null,
  }),
  actions: {
    open() {
      this.visible = true
    },
    close() {
      this.visible = false
    },
  },
})

