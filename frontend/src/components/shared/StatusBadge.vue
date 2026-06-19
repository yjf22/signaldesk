<script setup lang="ts">
import type { SourceStatus, SummaryStatus, ChangeType, FetchStatus } from '@/types/common';

type StatusType = SourceStatus | SummaryStatus | ChangeType | FetchStatus | 'SUCCESS';

const props = defineProps<{
  status: StatusType;
}>();

const statusConfig: Record<string, { label: string; className: string }> = {
  ACTIVE: { label: '活跃', className: 'bg-success-50 text-success-600' },
  PAUSED: { label: '已暂停', className: 'bg-gray-100 text-gray-600' },
  ARCHIVED: { label: '已归档', className: 'bg-gray-100 text-gray-500' },
  PENDING: { label: '等待中', className: 'bg-gray-100 text-gray-600' },
  GATHERING: { label: '采集中', className: 'bg-primary-50 text-primary-600' },
  PROMPTING: { label: '分析中', className: 'bg-primary-50 text-primary-600' },
  COMPLETED: { label: '已完成', className: 'bg-success-50 text-success-600' },
  FAILED: { label: '失败', className: 'bg-danger-50 text-danger-500' },
  NEW: { label: '新增', className: 'bg-primary-50 text-primary-600' },
  UPDATED: { label: '已更新', className: 'bg-warning-50 text-warning-600' },
  UNCHANGED: { label: '无变化', className: 'bg-gray-100 text-gray-500' },
  ERROR: { label: '错误', className: 'bg-danger-50 text-danger-500' },
  SUCCESS: { label: '成功', className: 'bg-success-50 text-success-600' },
  RUNNING: { label: '运行中', className: 'bg-primary-50 text-primary-600' },
};

const config = statusConfig[props.status] ?? { label: props.status, className: 'bg-gray-100 text-gray-600' };
</script>

<template>
  <span :class="['badge', config.className]">
    {{ config.label }}
  </span>
</template>
