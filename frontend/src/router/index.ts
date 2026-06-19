import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/LoginPage.vue'),
    meta: { guest: true },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/pages/RegisterPage.vue'),
    meta: { guest: true },
  },
  {
    path: '/',
    component: () => import('@/components/layout/AppLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/dashboard',
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/pages/DashboardPage.vue'),
      },
      {
        path: 'sources',
        name: 'SourceList',
        component: () => import('@/pages/SourceListPage.vue'),
      },
      {
        path: 'sources/new',
        name: 'SourceCreate',
        component: () => import('@/pages/SourceEditPage.vue'),
      },
      {
        path: 'sources/:id',
        name: 'SourceDetail',
        component: () => import('@/pages/SourceDetailPage.vue'),
      },
      {
        path: 'sources/:id/edit',
        name: 'SourceEdit',
        component: () => import('@/pages/SourceEditPage.vue'),
      },
      {
        path: 'search',
        name: 'Search',
        component: () => import('@/pages/SearchPage.vue'),
      },
      {
        path: 'summaries',
        name: 'SummaryHistory',
        component: () => import('@/pages/SummaryHistoryPage.vue'),
      },
      {
        path: 'summaries/:id',
        name: 'SummaryDetail',
        component: () => import('@/pages/SummaryDetailPage.vue'),
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/pages/NotFoundPage.vue'),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore();

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'Login', query: { redirect: to.fullPath } });
  } else if (to.meta.guest && authStore.isAuthenticated) {
    next({ name: 'Dashboard' });
  } else {
    next();
  }
});

export default router;
