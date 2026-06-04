import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      component: () => import('@/layouts/AuthLayout.vue'),
      children: [
        { path: '', component: () => import('@/pages/LoginPage.vue') },
      ],
    },
    {
      path: '/register',
      component: () => import('@/layouts/AuthLayout.vue'),
      children: [
        { path: '', component: () => import('@/pages/RegisterPage.vue') },
      ],
    },
    {
      path: '/',
      component: () => import('@/layouts/DefaultLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/books' },
        { path: 'books', component: () => import('@/pages/BooksPage.vue') },
        { path: 'books/new', component: () => import('@/pages/NewBookPage.vue') },
        { path: 'books/:id/edit', component: () => import('@/pages/EditBookPage.vue') },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return '/login'
  }
  if ((to.path === '/login' || to.path === '/register') && auth.isAuthenticated) {
    return '/books'
  }
})

export default router
