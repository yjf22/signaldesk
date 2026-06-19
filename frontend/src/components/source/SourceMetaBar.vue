<script setup lang="ts">
import { format } from 'date-fns';
import { zhCN } from 'date-fns/locale';

withDefaults(defineProps<{
  createdAt: string;
  lastFetchedAt?: string | null;
  totalDocuments?: number;
  fetchIntervalMin: number;
}>(), {
  lastFetchedAt: null,
  totalDocuments: 0,
});

function fmtDate(dateStr?: string | null): string {
  if (!dateStr) return '暂无';
  const date = new Date(dateStr);
  if (Number.isNaN(date.getTime())) return '暂无';
  return format(date, 'yyyy-MM-dd HH:mm', { locale: zhCN });
}

function fmtInterval(min: number): string {
  if (min < 60) return `${min} 分钟`;
  const hours = min / 60;
  if (hours < 24) return `${hours} 小时`;
  return `${hours / 24} 天`;
}
</script>

<template>
  <div class="mb-6 flex flex-wrap items-center gap-x-6 gap-y-2 text-xs text-gray-500">
    <span>创建于 {{ fmtDate(createdAt) }}</span>
    <span>最后抓取 {{ fmtDate(lastFetchedAt) }}</span>
    <span>{{ totalDocuments }} 篇文档</span>
    <span>抓取间隔: {{ fmtInterval(fetchIntervalMin) }}</span>
  </div>
</template>
