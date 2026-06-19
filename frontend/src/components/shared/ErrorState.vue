<script setup lang="ts">
defineProps<{
  title?: string;
  message: string;
}>();

defineEmits<{
  (e: 'retry'): void;
}>();
</script>

<template>
  <div class="flex flex-col items-center justify-center py-16 px-4 text-center">
    <div class="w-12 h-12 rounded-full bg-danger-50 flex items-center justify-center mb-4">
      <svg class="w-6 h-6 text-danger-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
    </div>
    <h3 class="text-lg font-semibold text-gray-900 mb-1">{{ title ?? '加载失败' }}</h3>
    <p class="text-sm text-gray-500 max-w-sm mb-6">{{ message }}</p>
    <button type="button" class="btn-secondary" @click="$emit('retry')">
      重新加载
    </button>
  </div>
</template>
