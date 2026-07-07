import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    adminSidebarCollapsed: false,
  }),
  actions: {
    toggleAdminSidebar() {
      this.adminSidebarCollapsed = !this.adminSidebarCollapsed
    },
  },
})

