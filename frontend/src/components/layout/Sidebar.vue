<script setup lang="ts">
import { useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const route = useRoute();
const authStore = useAuthStore();

const navItems = [
  { to: '/dashboard', label: '仪表盘', icon: '◫' },
  { to: '/sources', label: '信息源', icon: '⏻' },
  { to: '/search', label: '搜索', icon: '⌕' },
  { to: '/summaries', label: '摘要', icon: '✦' },
];

function isActive(path: string): boolean {
  if (path === '/dashboard') return route.path === '/dashboard';
  return route.path.startsWith(path);
}
</script>

<template>
  <aside class="w-56 bg-white border-r border-gray-200 flex flex-col h-screen sticky top-0">
    <!-- Logo -->
    <div class="h-14 flex items-center px-5 border-b border-gray-100">
      <div class="w-7 h-7 rounded-md bg-primary-600 flex items-center justify-center mr-2.5">
        <svg class="w-4 h-4 text-white" viewBox="0 0 32 32" fill="none">
          <path d="M8 12L16 6L24 12V22C24 23.1046 23.1046 24 22 24H10C8.89543 24 8 23.1046 8 22V12Z" fill="currentColor" fill-opacity="0.95"/>
          <circle cx="16" cy="16" r="3" fill="#2563EB"/>
        </svg>
      </div>
      <span class="font-semibold text-gray-900 text-sm">SignalDesk</span>
    </div>

    <!-- Nav items -->
    <nav class="flex-1 px-3 py-4 space-y-0.5">
      <router-link
        v-for="item in navItems"
        :key="item.to"
        :to="item.to"
        class="flex items-center gap-3 px-3 py-2 rounded-md text-sm font-medium transition-colors"
        :class="isActive(item.to)
          ? 'bg-primary-50 text-primary-700'
          : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'"
      >
        <span class="text-base w-5 text-center">{{ item.icon }}</span>
        {{ item.label }}
      </router-link>
    </nav>

    <!-- User footer -->
    <div class="border-t border-gray-100 px-4 py-3">
      <div class="flex items-center gap-3">
        <div class="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center text-primary-600 text-xs font-semibold">
          {{ authStore.user?.username?.charAt(0)?.toUpperCase() ?? 'U' }}
        </div>
        <div class="flex-1 min-w-0">
          <p class="text-sm font-medium text-gray-900 truncate">{{ authStore.user?.username ?? '用户' }}</p>
          <p class="text-xs text-gray-500 truncate">{{ authStore.user?.email ?? '' }}</p>
        </div>
      </div>
      <button
        class="mt-2 w-full text-xs text-gray-500 hover:text-gray-700 text-left transition-colors"
        @click="authStore.logout()"
      >
        退出登录
      </button>
    </div>
  </aside>
</template>
