<script setup lang="ts">
defineProps<{
  title: string;
  subtitle?: string;
}>();
</script>

<template>
  <div class="min-h-screen flex items-center justify-center px-4 py-12 bg-gray-50">
    <div class="w-full max-w-md">
      <div class="text-center mb-8">
        <div class="w-10 h-10 rounded-lg bg-primary-600 flex items-center justify-center mx-auto mb-3">
          <svg class="w-5 h-5 text-white" viewBox="0 0 32 32" fill="none">
            <path d="M8 12L16 6L24 12V22C24 23.1046 23.1046 24 22 24H10C8.89543 24 8 23.1046 8 22V12Z" fill="currentColor" fill-opacity="0.95"/>
            <circle cx="16" cy="16" r="3" fill="#2563EB"/>
          </svg>
        </div>
        <h1 class="text-xl font-bold text-gray-900">SignalDesk</h1>
        <p class="text-sm text-gray-500 mt-1">个人情报工作台</p>
      </div>

      <div class="card p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-1">{{ title }}</h2>
        <p v-if="subtitle" class="text-sm text-gray-500 mb-5">{{ subtitle }}</p>
        <slot />
      </div>

      <p class="text-center text-xs text-gray-400 mt-6">
        &copy; {{ new Date().getFullYear() }} SignalDesk
      </p>
    </div>
  </div>
</template>
