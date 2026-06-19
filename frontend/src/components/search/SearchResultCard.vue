<script setup lang="ts">
import { computed } from 'vue';
import { formatDistanceToNow } from 'date-fns';
import { zhCN } from 'date-fns/locale';
import type { SearchResultResponse } from '@/types/search';

const props = defineProps<{
  result: SearchResultResponse;
  keyword: string;
}>();

function timeAgo(dateStr: string): string {
  return formatDistanceToNow(new Date(dateStr), { addSuffix: true, locale: zhCN });
}

// Simple keyword highlighting
function highlightText(text: string, keyword: string): string {
  if (!keyword) return text;
  const escaped = keyword.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  return text.replace(
    new RegExp(`(${escaped})`, 'gi'),
    '<mark class="bg-warning-100 text-warning-800 rounded px-0.5">$1</mark>',
  );
}
</script>

<template>
  <div class="card-hover p-4">
    <h4 class="text-sm font-medium text-gray-900 mb-1" v-html="highlightText(result.title, keyword)" />
    <div class="flex items-center gap-2 mb-2">
      <span class="badge bg-gray-100 text-gray-600 text-[10px]">{{ result.sourceTitle }}</span>
      <span class="text-xs text-gray-400">{{ timeAgo(result.fetchedAt) }}</span>
    </div>
    <p class="text-xs text-gray-500 line-clamp-3" v-html="highlightText(result.snippet, keyword)" />
    <div v-if="result.tags?.length" class="flex gap-1 mt-2">
      <span
        v-for="tag in result.tags"
        :key="tag"
        class="badge bg-gray-50 text-gray-500 text-[10px]"
      >
        {{ tag }}
      </span>
    </div>
  </div>
</template>
