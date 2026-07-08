import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import MainLayout from '@/layouts/MainLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import OfficialLayout from '@/layouts/OfficialLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: '', name: 'HomePage', component: () => import('@/views/home/HomePage.vue') },
      { path: 'movies', name: 'MovieListPage', component: () => import('@/views/movie/MovieListPage.vue') },
      { path: 'movies/:id', name: 'MovieDetailPage', component: () => import('@/views/movie/MovieDetailPage.vue') },
      { path: 'news', name: 'NewsListPage', component: () => import('@/views/news/NewsListPage.vue') },
      { path: 'news/:id', name: 'NewsDetailPage', component: () => import('@/views/news/NewsDetailPage.vue') },
      { path: 'long-reviews', name: 'LongReviewListPage', component: () => import('@/views/review/LongReviewListPage.vue') },
      { path: 'long-reviews/create', name: 'LongReviewCreatePage', component: () => import('@/views/review/LongReviewCreatePage.vue'), meta: { requiresAuth: true } },
      { path: 'long-reviews/:id', name: 'LongReviewDetailPage', component: () => import('@/views/review/LongReviewDetailPage.vue') },
      { path: 'merchandise', name: 'MerchandiseListPage', component: () => import('@/views/merchandise/MerchandiseListPage.vue') },
    ],
  },
  { path: '/login', name: 'LoginPage', component: () => import('@/views/auth/LoginPage.vue') },
  { path: '/register', name: 'RegisterPage', component: () => import('@/views/auth/RegisterPage.vue') },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, roles: ['ADMIN', 'SUPER_ADMIN'] },
    children: [
      { path: '', name: 'AdminDashboardPage', component: () => import('@/views/admin/AdminDashboardPage.vue') },
      { path: 'audits', name: 'AuditListPage', component: () => import('@/views/admin/AuditListPage.vue') },
      { path: 'reports', name: 'ReportHandlePage', component: () => import('@/views/admin/ReportHandlePage.vue') },
    ],
  },
  {
    path: '/official',
    component: OfficialLayout,
    meta: { requiresAuth: true, roles: ['OFFICIAL', 'SUPER_ADMIN'] },
    children: [
      { path: '', name: 'OfficialDashboardPage', component: () => import('@/views/official/OfficialDashboardPage.vue') },
      { path: 'certification', name: 'CertificationPage', component: () => import('@/views/official/CertificationPage.vue') },
      { path: 'submissions', name: 'MySubmissionPage', component: () => import('@/views/official/MySubmissionPage.vue') },
    ],
  },
  { path: '/403', name: 'ForbiddenPage', component: () => import('@/views/error/ForbiddenPage.vue') },
  { path: '/:pathMatch(.*)*', name: 'NotFoundPage', component: () => import('@/views/error/NotFoundPage.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to) => {
  const userStore = useUserStore()
  if (userStore.token && !userStore.userInfo) {
    await userStore.fetchCurrentUser().catch(() => userStore.logout())
  }
  if (to.meta.requiresAuth && !userStore.isLogin) {
    return '/login'
  }
  const roles = (to.meta.roles as string[] | undefined) || []
  if (roles.length && !userStore.hasAnyRole(roles)) {
    return '/403'
  }
  return true
})

export default router

