<script setup lang="ts">
defineProps<{
  count?: number;
}>();
</script>

<template>
  <div class="space-y-3">
    <div
      v-for="i in (count ?? 5)"
      :key="i"
      class="card p-4 space-y-3"
    >
      <div class="flex items-center gap-3">
        <div class="skeleton w-8 h-8 rounded" />
        <div class="flex-1 space-y-2">
          <div class="skeleton h-4 w-3/4" />
          <div class="skeleton h-3 w-1/2" />
        </div>
      </div>
      <div class="skeleton h-3 w-full" />
      <div class="flex gap-2">
        <div class="skeleton h-5 w-12 rounded-full" />
        <div class="skeleton h-5 w-16 rounded-full" />
      </div>
    </div>
  </div>
</template>
