<script setup lang="ts">
import { computed } from 'vue';
import { formatDistanceToNow } from 'date-fns';
import { zhCN } from 'date-fns/locale';
import { useRouter } from 'vue-router';
import StatusBadge from '@/components/shared/StatusBadge.vue';
import TagChips from '@/components/shared/TagChips.vue';
import type { SourceResponse } from '@/types/source';

const props = withDefaults(defineProps<{
  source: SourceResponse;
  selectable?: boolean;
  selected?: boolean;
}>(), {
  selectable: false,
  selected: false,
});

const emit = defineEmits<{
  (e: 'toggle-select', id: number): void;
}>();

const router = useRouter();

const sourceIcon = computed(() => {
  const icons: Record<string, string> = {
    URL: '🌐',
    RSS: '📰',
    PDF: '📄',
    NOTE: '📝',
  };
  return icons[props.source.sourceType] ?? '📦';
});

const displayUrl = computed(() => {
  if (!props.source.url) return null;
  try {
    const u = new URL(props.source.url);
    return u.hostname + u.pathname;
  } catch {
    return props.source.url;
  }
});

function timeAgo(dateStr: string | null): string {
  if (!dateStr) return '从未抓取';
  return formatDistanceToNow(new Date(dateStr), { addSuffix: true, locale: zhCN });
}

function hasChange(): boolean {
  return !!props.source.lastChangeAt;
}

function goToDetail() {
  router.push(`/sources/${props.source.id}`);
}

function goToEdit(e: Event) {
  e.stopPropagation();
  router.push(`/sources/${props.source.id}/edit`);
}

function toggleSelect(e: Event) {
  e.stopPropagation();
  emit('toggle-select', props.source.id);
}
</script>

<template>
  <div
    class="card-hover cursor-pointer p-4 transition"
    :class="selected ? 'ring-2 ring-primary-400 bg-primary-50/30' : ''"
    @click="goToDetail"
  >
    <div class="mb-3 flex items-start justify-between">
      <div class="flex min-w-0 items-center gap-2.5">
        <input
          v-if="selectable"
          type="checkbox"
          class="mt-0.5 h-4 w-4 rounded border-gray-300 text-primary-600 focus:ring-primary-500"
          :checked="selected"
          @click.stop
          @change="toggleSelect"
        />
        <span class="flex-shrink-0 text-xl">{{ sourceIcon }}</span>
        <div class="min-w-0">
          <h3 class="truncate text-sm font-medium text-gray-900">{{ source.title }}</h3>
          <p v-if="displayUrl" class="truncate text-xs text-gray-400">{{ displayUrl }}</p>
        </div>
      </div>

      <div class="ml-2 flex flex-shrink-0 items-center gap-2">
        <span
          v-if="hasChange()"
          class="inline-block h-2 w-2 rounded-full bg-warning-500"
          title="有更新"
        />
        <StatusBadge :status="source.status" />
      </div>
    </div>

    <TagChips v-if="source.tags?.length" :tags="source.tags" class="mb-2.5" />

    <div class="flex items-center justify-between">
      <p class="text-xs text-gray-400">
        抓取：{{ timeAgo(source.lastFetchedAt) }}
      </p>
      <button
        class="text-xs text-gray-400 transition-colors hover:text-gray-600"
        @click="goToEdit"
      >
        编辑
      </button>
    </div>
  </div>
</template>
