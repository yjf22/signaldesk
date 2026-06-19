<script setup lang="ts">
defineProps<{
  icon?: string;
  title: string;
  description?: string;
  actionLabel?: string;
}>();

defineEmits<{
  (e: 'action'): void;
}>();
</script>

<template>
  <div class="flex flex-col items-center justify-center py-16 px-4 text-center">
    <div class="text-5xl mb-4">{{ icon ?? '📭' }}</div>
    <h3 class="text-lg font-semibold text-gray-900 mb-1">{{ title }}</h3>
    <p v-if="description" class="text-sm text-gray-500 max-w-sm mb-6">{{ description }}</p>
    <button v-if="actionLabel" type="button" class="btn-primary" @click="$emit('action')">
      {{ actionLabel }}
    </button>
  </div>
</template>
