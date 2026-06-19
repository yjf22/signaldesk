<script setup lang="ts">
import type { ChangeType } from '@/types/common';

const props = withDefaults(defineProps<{
  type: ChangeType;
  showLabel?: boolean;
}>(), {
  showLabel: true,
});

const indicatorConfig: Record<string, { dotColor: string; label: string; badgeClass: string }> = {
  NEW: { dotColor: 'bg-primary-500', label: '新', badgeClass: 'bg-primary-50 text-primary-600' },
  UPDATED: { dotColor: 'bg-warning-500', label: '已更新', badgeClass: 'bg-warning-50 text-warning-600' },
  UNCHANGED: { dotColor: 'bg-gray-300', label: '', badgeClass: '' },
  ERROR: { dotColor: 'bg-danger-500', label: '失败', badgeClass: 'bg-danger-50 text-danger-500' },
};

const config = indicatorConfig[props.type] ?? indicatorConfig.UNCHANGED;
</script>

<template>
  <span class="inline-flex items-center gap-1.5">
    <span class="inline-block w-2 h-2 rounded-full" :class="config.dotColor" />
    <span v-if="showLabel && config.label" class="badge text-[11px]" :class="config.badgeClass">
      {{ config.label }}
    </span>
  </span>
</template>
