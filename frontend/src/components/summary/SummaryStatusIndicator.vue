<script setup lang="ts">
import type { SummaryStatus } from '@/types/common';

defineProps<{
  status: SummaryStatus;
}>();

const statusMap: Record<string, { icon: string; text: string; className: string }> = {
  PENDING: { icon: '⏳', text: '正在准备生成摘要...', className: 'text-gray-500' },
  GATHERING: { icon: '🔍', text: '正在收集相关内容...', className: 'text-primary-500' },
  PROMPTING: { icon: '🤖', text: 'AI 正在分析内容...', className: 'text-primary-500' },
  COMPLETED: { icon: '✅', text: '摘要生成完成', className: 'text-success-500' },
  FAILED: { icon: '❌', text: '摘要生成失败', className: 'text-danger-500' },
};
</script>

<template>
  <div class="flex items-center gap-3 py-4">
    <span
      v-if="status === 'GATHERING' || status === 'PROMPTING'"
      class="inline-block w-5 h-5 border-2 border-primary-300 border-t-primary-600 rounded-full animate-spin"
    />
    <span v-else class="text-xl">{{ statusMap[status]?.icon ?? '⏳' }}</span>
    <span :class="['text-sm font-medium', statusMap[status]?.className ?? 'text-gray-500']">
      {{ statusMap[status]?.text ?? status }}
    </span>
  </div>
</template>
