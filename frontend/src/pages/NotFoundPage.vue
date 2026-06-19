<script setup lang="ts">
import { useRouter } from 'vue-router';

const router = useRouter();
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50">
    <div class="text-center">
      <h1 class="text-6xl font-bold text-gray-200 mb-4">404</h1>
      <h2 class="text-lg font-semibold text-gray-900 mb-2">页面不存在</h2>
      <p class="text-sm text-gray-500 mb-6">你访问的页面可能已被删除或移动。</p>
      <button class="btn-primary" @click="router.push('/dashboard')">
        返回仪表盘
      </button>
    </div>
  </div>
</template>
