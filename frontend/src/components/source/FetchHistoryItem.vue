<script setup lang="ts">
import { format } from 'date-fns';
import { zhCN } from 'date-fns/locale';
import type { FetchTaskResponse } from '@/types/source';

defineProps<{
  task: FetchTaskResponse;
}>();

function fmtDate(dateStr: string | null | undefined): string {
  if (!dateStr) return '暂无';
  const date = new Date(dateStr);
  if (Number.isNaN(date.getTime())) return '暂无';
  return format(date, 'MM-dd HH:mm:ss', { locale: zhCN });
}

function statusIcon(status: string): string {
  switch (status) {
    case 'COMPLETED':
      return '✓';
    case 'FAILED':
      return '✕';
    case 'FETCHING':
    case 'PARSING':
    case 'PENDING':
      return '⏳';
    case 'CANCELLED':
      return '⏹';
    default:
      return '•';
  }
}

function statusText(status: string): string {
  switch (status) {
    case 'PENDING':
      return '等待中';
    case 'FETCHING':
      return '抓取中';
    case 'PARSING':
      return '解析中';
    case 'COMPLETED':
      return '已完成';
    case 'FAILED':
      return '失败';
    case 'CANCELLED':
      return '已取消';
    default:
      return status;
  }
}
</script>

<template>
  <div class="flex items-start gap-3 border-b border-gray-100 py-3 last:border-0">
    <span class="mt-0.5 text-sm">{{ statusIcon(task.status) }}</span>
    <div class="min-w-0 flex-1">
      <div class="mb-1 flex items-center gap-2">
        <span
          class="badge text-[10px]"
          :class="task.triggerType === 'MANUAL' ? 'bg-blue-50 text-blue-600' : 'bg-gray-100 text-gray-600'"
        >
          {{ task.triggerType === 'MANUAL' ? '手动' : '定时' }}
        </span>
        <span class="badge text-[10px] bg-gray-100 text-gray-600">
          {{ statusText(task.status) }}
        </span>
        <span class="text-xs text-gray-400">{{ fmtDate(task.completedAt || task.startedAt || task.createdAt) }}</span>
      </div>
      <p v-if="task.resultMessage" class="text-xs text-gray-600">{{ task.resultMessage }}</p>
      <p v-if="task.errorMessage" class="text-xs text-danger-500">{{ task.errorMessage }}</p>
    </div>
  </div>
</template>
