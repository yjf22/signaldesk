<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useSourceStore } from '@/stores/sources';
import SourceCard from '@/components/source/SourceCard.vue';
import SourceFilterBar from '@/components/source/SourceFilterBar.vue';
import EmptyState from '@/components/shared/EmptyState.vue';
import ErrorState from '@/components/shared/ErrorState.vue';
import LoadingSkeleton from '@/components/shared/LoadingSkeleton.vue';

const router = useRouter();
const store = useSourceStore();

const selectedIds = ref<number[]>([]);
const batchLoading = ref(false);

onMounted(() => {
  store.fetchSources();
});

watch([() => store.filter.status, () => store.filter.type, () => store.filter.sort], () => {
  selectedIds.value = [];
  store.fetchSources();
});

const isEmpty = () => !store.loading && !store.error && store.sources.length === 0;
const isEmptyFiltered = () => isEmpty() && (store.filter.status || store.filter.type);
const selectedCount = computed(() => selectedIds.value.length);
const allSelected = computed(() => store.sources.length > 0 && selectedIds.value.length === store.sources.length);
const selectedSources = computed(() => store.sources.filter((source) => selectedIds.value.includes(source.id)));
const canBatchPause = computed(() => selectedSources.value.some((source) => source.status !== 'PAUSED'));
const canBatchResume = computed(() => selectedSources.value.some((source) => source.status !== 'ACTIVE'));

function toggleSelect(id: number) {
  if (selectedIds.value.includes(id)) {
    selectedIds.value = selectedIds.value.filter((item) => item !== id);
  } else {
    selectedIds.value = [...selectedIds.value, id];
  }
}

function toggleSelectAll() {
  selectedIds.value = allSelected.value ? [] : store.sources.map((source) => source.id);
}

async function handleBatchPause() {
  if (!selectedIds.value.length || !canBatchPause.value) return;
  batchLoading.value = true;
  try {
    await store.updateStatuses(selectedIds.value, 'PAUSED');
    selectedIds.value = [];
  } finally {
    batchLoading.value = false;
  }
}

async function handleBatchResume() {
  if (!selectedIds.value.length || !canBatchResume.value) return;
  batchLoading.value = true;
  try {
    await store.updateStatuses(selectedIds.value, 'ACTIVE');
    selectedIds.value = [];
  } finally {
    batchLoading.value = false;
  }
}

function buildDeleteConfirmMessage() {
  const previewTitles = selectedSources.value
    .slice(0, 5)
    .map((source) => `- ${source.title}`)
    .join('\n');

  const moreCount = selectedSources.value.length - Math.min(selectedSources.value.length, 5);
  const moreLine = moreCount > 0 ? `\n- 以及另外 ${moreCount} 个信息源` : '';

  return `确定要删除这 ${selectedIds.value.length} 个信息源吗？\n\n${previewTitles}${moreLine}\n\n删除后不可恢复，相关抓取记录和摘要也会一起丢失。`;
}

async function handleBatchDelete() {
  if (!selectedIds.value.length) return;
  const confirmed = window.confirm(buildDeleteConfirmMessage());
  if (!confirmed) return;

  batchLoading.value = true;
  try {
    await store.deleteSources(selectedIds.value);
    selectedIds.value = [];
  } finally {
    batchLoading.value = false;
  }
}
</script>

<template>
  <div>
    <div class="mb-6 flex items-center justify-between">
      <h1 class="text-2xl font-bold text-gray-900">信息源</h1>
      <button class="btn-primary" @click="router.push('/sources/new')">
        + 添加信息源
      </button>
    </div>

    <SourceFilterBar
      v-model:status="store.filter.status"
      v-model:type="store.filter.type"
      v-model:sort="store.filter.sort"
      @change="store.fetchSources()"
    />

    <div
      v-if="store.sources.length"
      class="mb-4 flex flex-wrap items-center gap-3 rounded-lg border border-gray-200 bg-white px-4 py-3"
    >
      <label class="flex items-center gap-2 text-sm text-gray-600">
        <input
          type="checkbox"
          class="h-4 w-4 rounded border-gray-300 text-primary-600 focus:ring-primary-500"
          :checked="allSelected"
          @change="toggleSelectAll"
        />
        <span>全选当前页</span>
      </label>

      <span class="text-sm text-gray-500">已选 {{ selectedCount }} 项</span>

      <button
        class="btn-secondary btn-sm"
        :disabled="selectedCount === 0 || batchLoading || !canBatchPause"
        @click="handleBatchPause"
      >
        批量暂停
      </button>

      <button
        class="btn-secondary btn-sm"
        :disabled="selectedCount === 0 || batchLoading || !canBatchResume"
        @click="handleBatchResume"
      >
        批量恢复
      </button>

      <button
        class="btn-secondary btn-sm text-danger-600 hover:text-danger-700"
        :disabled="selectedCount === 0 || batchLoading"
        @click="handleBatchDelete"
      >
        批量删除
      </button>
    </div>

    <LoadingSkeleton v-if="store.loading && !store.sources.length" :count="6" />

    <ErrorState
      v-else-if="store.error && !store.sources.length"
      :message="store.error"
      @retry="store.fetchSources()"
    />

    <EmptyState
      v-else-if="isEmptyFiltered()"
      title="当前筛选条件下没有信息源"
      description="可以调整筛选条件，或者清空筛选后再看。"
      action-label="清空筛选"
      @action="store.setFilter({ status: '', type: '' })"
    />

    <EmptyState
      v-else-if="isEmpty()"
      icon="📰"
      title="还没有信息源"
      description="添加 URL、RSS、PDF 或笔记，开始追踪内容变化。"
      action-label="添加第一个信息源"
      @action="router.push('/sources/new')"
    />

    <div v-else class="grid grid-cols-1 gap-3 sm:grid-cols-2">
      <SourceCard
        v-for="source in store.sources"
        :key="source.id"
        :source="source"
        selectable
        :selected="selectedIds.includes(source.id)"
        @toggle-select="toggleSelect"
      />
    </div>
  </div>
</template>
