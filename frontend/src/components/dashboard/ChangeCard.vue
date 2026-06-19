<script setup lang="ts">
import { formatDistanceToNow } from 'date-fns';
import { zhCN } from 'date-fns/locale';
import { useRouter } from 'vue-router';
import ChangeIndicator from '@/components/shared/ChangeIndicator.vue';
import type { RecentChange } from '@/api/dashboard';

const props = defineProps<{
  change: RecentChange;
}>();

const router = useRouter();

function timeAgo(dateStr: string): string {
  return formatDistanceToNow(new Date(dateStr), { addSuffix: true, locale: zhCN });
}

function goToSource() {
  router.push(`/sources/${props.change.sourceId}`);
}
</script>

<template>
  <div class="card-hover cursor-pointer p-4" @click="goToSource">
    <div class="flex items-start justify-between mb-2">
      <div class="flex-1 min-w-0">
        <h4 class="text-sm font-medium text-gray-900 truncate">{{ change.sourceTitle }}</h4>
      </div>
      <ChangeIndicator :type="change.changeType" />
    </div>
    <p class="text-xs text-gray-500 line-clamp-2 mb-2">{{ change.snippet }}</p>
    <p class="text-xs text-gray-400">{{ change.changedAt ? timeAgo(change.changedAt) : '刚刚' }}</p>
  </div>
</template>
