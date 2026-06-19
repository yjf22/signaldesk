<script setup lang="ts">
import { formatDistanceToNow } from 'date-fns';
import { zhCN } from 'date-fns/locale';
import { useRouter } from 'vue-router';
import StatusBadge from '@/components/shared/StatusBadge.vue';
import type { SummaryResponse } from '@/types/summary';

const props = defineProps<{
  summary: SummaryResponse;
}>();

const router = useRouter();

function timeAgo(dateStr: string): string {
  return formatDistanceToNow(new Date(dateStr), { addSuffix: true, locale: zhCN });
}
</script>

<template>
  <div
    class="card-hover p-4 cursor-pointer"
    @click="router.push(`/summaries/${summary.id}`)"
  >
    <div class="flex items-start justify-between mb-2">
      <h4 class="text-sm font-medium text-gray-900 flex-1 min-w-0 pr-2">
        {{ summary.title || '未命名摘要' }}
      </h4>
      <StatusBadge :status="summary.status" />
    </div>
    <p class="text-xs text-gray-500 line-clamp-2 mb-2">
      {{ summary.contentMd?.slice(0, 200) || '等待生成...' }}
    </p>
    <p class="text-xs text-gray-400">{{ timeAgo(summary.createdAt) }}</p>
  </div>
</template>
