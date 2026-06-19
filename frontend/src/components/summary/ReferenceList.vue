<script setup lang="ts">
import type { SummaryReference } from '@/types/summary';

defineProps<{
  references: SummaryReference[];
}>();
</script>

<template>
  <div v-if="references.length" class="mt-6">
    <h4 class="text-sm font-semibold text-gray-900 mb-2">引用来源</h4>
    <div class="space-y-2">
      <div
        v-for="ref in references"
        :key="ref.refIndex"
        class="text-xs text-gray-600 bg-gray-50 rounded-md px-3 py-2"
      >
        <span class="font-medium text-primary-600">[{{ ref.refIndex }}]</span>
        <span class="ml-1">{{ ref.sourceTitle }}</span>
        <span class="text-gray-400 ml-2">— {{ ref.quoteText.slice(0, 80) }}{{ ref.quoteText.length > 80 ? '...' : '' }}</span>
      </div>
    </div>
  </div>
</template>
