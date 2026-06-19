<script setup lang="ts">
import { computed } from 'vue';
import { format } from 'date-fns';
import { zhCN } from 'date-fns/locale';
import ChangeIndicator from '@/components/shared/ChangeIndicator.vue';
import type { ChangeType } from '@/types/common';
import type { DocumentResponse } from '@/types/document';

const props = defineProps<{
  doc: DocumentResponse;
}>();

function fmtDate(dateStr?: string | null): string {
  if (!dateStr) return '时间未知';
  const date = new Date(dateStr);
  if (Number.isNaN(date.getTime())) return '时间未知';
  return format(date, 'yyyy-MM-dd HH:mm', { locale: zhCN });
}

const previewText = computed(() => props.doc.contentPreview ?? props.doc.contentText ?? '暂无内容预览');
const displayTime = computed(() => props.doc.fetchedAt ?? props.doc.publishedAt ?? props.doc.createdAt);
const changeType = computed<ChangeType>(() => props.doc.changeType ?? 'NEW');
const sourceUrl = computed(() => props.doc.sourceUrl?.trim() || '');
const canOpenSource = computed(() => /^https?:\/\//i.test(sourceUrl.value));

function openSource() {
  if (!canOpenSource.value) return;
  window.open(sourceUrl.value, '_blank', 'noopener,noreferrer');
}
</script>

<template>
  <div
    class="card p-4 transition-shadow"
    :class="canOpenSource ? 'cursor-pointer hover:shadow-md' : ''"
    @click="openSource"
  >
    <div class="mb-2 flex items-start justify-between">
      <h4 class="min-w-0 flex-1 pr-2 text-sm font-medium text-gray-900">
        {{ doc.title || '无标题' }}
      </h4>
      <ChangeIndicator :type="changeType" />
    </div>
    <div class="mb-2 flex items-center gap-3 text-xs text-gray-400">
      <span>{{ doc.wordCount ?? 0 }} 字</span>
      <span>{{ fmtDate(displayTime) }}</span>
    </div>
    <p class="line-clamp-3 text-xs text-gray-500">{{ previewText }}</p>
    <div v-if="canOpenSource" class="mt-3 flex items-center justify-between gap-3 text-xs">
      <span class="truncate text-primary-600">{{ sourceUrl }}</span>
      <button
        type="button"
        class="shrink-0 text-primary-600 hover:text-primary-700 hover:underline"
        @click.stop="openSource"
      >
        查看原文
      </button>
    </div>
  </div>
</template>
